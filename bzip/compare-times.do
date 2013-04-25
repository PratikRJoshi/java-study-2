*! stata

/**
 * Compare the timing results of the array and linked-list branches.
 * Negative, statistically significant values on "branch" in regression output
 * favor the linked-list branch. Positive, statistically significant values on
 * "branch" favor the array branch.
 */

 quietly {
	version 11.2
	clear all

	label define action 0 "+-" 1 "-" 2 "+"
	label define branch 0 "array" 1 "ll"
	label define binary 0 "text" 1 "binary"

	// Read in a single file and mark which it is in the data set
	// Usage: readinone branch-name
	program define readinone, nclass
		args branch
		insheet using "`branch'.csv", comma clear double
		generate byte branch = "`branch'":branch
		label variable branch "git branch tested"
	end

	// Name the columns output by timer.py and tag the branch
	// Usage: namevars
	// Return: r(class) == name of class
	program define namevars, rclass
		rename v1 class
		rename v2 file
		label variable file "file read in for this test"
		rename v3 bytes
		label variable bytes "size of file read in for this test"
		encode v4, generate(action) label(action)
		drop v4
		label variable action "-: encode, +: decode, +-: encode then decode"
		rename v5 time
		label variable time "time in seconds test took"

		generate byte binary = substr(file, -4, 4) != ".txt"
		label variable binary "file was binary rather than english text"

		label values branch branch
		label values binary binary

		compress
		assert class[_n] == class[_n - 1] if _n > 1
		return local class = class[1]
		drop class
	end

	// Read in both data sets and merge them
	// Usage: loadall
	program define loadall, nclass
		readinone array
		tempfile array
		save `array'
		readinone ll
		append using `array'
		namevars
		label data "Timing tests of different implementations of `r(class)'"
	end

	// Load the data and run regressions of time on branch
	// Usage: main
	program define main, nclass
		quietly loadall
		describe
		bysort action: regress time bytes binary##branch
	end
}

main
