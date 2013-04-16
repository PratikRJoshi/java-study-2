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
	private final int[] w, l, r; // Wins, losses, remaining games
	private final int[][] g; // Games left between teams i and j

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
		for (int i = 0; i < n; i++) {
			teams.put(file.readString(), i);
			w[i] = file.readInt();
			l[i] = file.readInt();
			r[i] = file.readInt();
			for (int j = 0; j < n; j++)
				g[i][j] = file.readInt();
		}
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

	// is given team eliminated?
	public boolean isEliminated(String team);

	// subset R of teams that eliminates given team; null if not eliminated
	public Iterable<String> certificateOfElimination(String team);

	// Throw IllegalArgumentException if team name not recognized.
	private void isTeam(String team) {
		if (!teams.containsKey(team))
			throw new IllegalArgumentException("Unrecognized team: " + team);
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
