/**
 * Determine which teams in an MLB division are mathematically eliminated from
 * wining the division by the end of the regular season.
 * <p>
 * Usage: java BaseballElimination inputfile.txt
 *
 * @author William Schwartz
 */
public class BaseballElimination {
	private final HashMap<String, Integer> teams; // Map team name to index
	private final String[] ids; // Map index to team name
	private final int[] w, l, r; // Wins, losses, remaining games
	private final int[][] g; // Games left between teams i and j
	private Result last; // Cached last result
	private final int mostWins, leader; // For making trivialSearch() O(1)

	/**
	 * Create a baseball division from given filename in format specified below.
	 * <p>
	 * The input format is the number of teams in the division N followed by one
	 * line for each team. Each line contains the team name (with no internal
	 * whitespace characters), the number of wins, the number of losses, the
	 * number of remaining games, and the number of remaining games against each
	 * team in the divsion.
	 *
	 * @param filename The name of the file to read in as input.
	 */
	public BaseballElimination(String filename) {
		In file = new In(filename);
		int n = file.readInt();
		teams = new HashMap<String, Integer>(n + 1, 1.0); // Will never rehash
		w = new int[n];
		l = new int[n];
		r = new int[n];
		g = new int[n][n]; // XXX may be able to cut this in half (upper triangle)
		ids = new String[n];
		String name;
		int mostWins = 0, leader = 0;
		for (int i = 0; i < n; i++) {
			name = file.readString();
			teams.put(name, i);
			ids[i] = name;
			w[i] = file.readInt();
			l[i] = file.readInt();
			r[i] = file.readInt();
			for (int j = 0; j < n; j++)
				g[i][j] = file.readInt();
			if (w[i] > mostWins) {
				mostWins = w[i];
				leader = i;
			}
		}
		this.mostWins = mostWins;
		this.leader = leader;
	}

	/**
	 * Return the number of teams in the division.
	 */
	public int numberOfTeams() { return teams.size(); }

	/**
	 * Return iterable of team names.
	 */
	public Iterable<String> teams() { return teams.keySet(); }

	/**
	 * Return the number of wins <code>team</code> has already had.
	 */
	public int wins(String team) {
		isTeam(team);
		return w[teams.get(team)];
	}

	/**
	 * Return the number of losses <code>team</code> has already had.
	 */
	public int losses(String team) {
		isTeam(team);
		return l[teams.get(team)];
	}

	/**
	 * Return the number of remaining games <code>team</code> has already had
	 * left.
	 */
	public int remaining(String team) {
		isTeam(team);
		return r[teams.get(team)];
	}

	/**
	 * Return the number of games that were already remaining left to play
	 * between <code>team1</code> and <code>team2</code>.
	 */
	public int against(String team1, String team2) {
		isTeam(team);
		return g[teams.get(team1)][teams.get(team2)];
	}

	/**
	 * Return true if the given team cannot finish the season in first place in
	 * the division.
	 */
	public boolean isEliminated(String team) {
		solve(team);
		return last.eliminated;
	}

	/**
	 * If the given team cannot finish the season in first place in the division
	 * return an iterable of the names of the teams that beat it out. Otherwise
	 * return <code>null</code>.
	 */
	public Iterable<String> certificateOfElimination(String team) {
		solve(team);
		return last.betterTeams;
	}

	// Throw IllegalArgumentException if team name not recognized.
	private void isTeam(String team) {
		if (!teams.containsKey(team))
			throw new IllegalArgumentException("Unrecognized team: " + team);
	}

	// For holding the result of an elimination search. Default/"zeroed" Result
	// is an uneliminated team. To eliminate the team, simply add teams better
	// than it.
	private class Result {
		private final String team;
		private boolean eliminated;
		private Bag<String> betterTeams;

		public Result(int id) { this.team = ids[id]; }

		public void addBetterTeam(int id) {
			assert id != teams.get(team);
			if (cert == null) {
				betterTeams = new Bag<String>();
				eliminated = true;
			}
			betterTeams.add(ids[id]);
		}
	}

	// If no or wrong solution cached, first try a trivial solution, then do a
	// full search, and cache the result.
	private void solve(String team) {
		if (last != null && last.team == team)
			return;
		isTeam(team);
		int id = teams.get(team);
		last = trivialSearch(id);
		if (last == null)
			last = fullSearch(id);
	}

	// Check if a team is trivially eliminated, i.e., some other team has
	// already won more than this team could in the rest of the season. The
	// running time is constant in the worst case.
	private Result trivialSearch(int id) {
		if (id != leader && w[id] + r[id] < mostWins) {
			Result result = new Result(id);
			result.addBetterTeam(leader);
			return Result;
		}
		return null;
	}

	// Do a full max-flow/min-cut search to determine if the team is eliminated
	// The running time is O(n * (n + 3 * (n - 1) * (n - 2) / 2)) in the worst
	// case (i.e., O(n^3)).
	private Result fullSearch(int id) {
		FordFulkerson maxFlow = buildGraphFor(id);
		Result result = new Result(team);
		if (isEliminated(id, maxFlow.value())) {
			for (int i = 0; i < numberOfTeams(); i++)
				if (maxFlow.inCut(i)) // XXX Maybe should be negated? Needs test
					result.addBetterTeam(i);
		}
		return result;
	}

	/**
	 * Read in a sports division from an input file and print out whether each
	 * team is eliminated and a certificate of elimination for each such team.
	 *
	 * @author Kevin Wayne
	 */
	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team))
					StdOut.print(t + " ");
				StdOut.println("}");
			}
			else
				StdOut.println(team + " is not eliminated");
		}
	}
}
