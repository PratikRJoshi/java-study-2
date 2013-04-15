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

	// number of teams
	public int numberOfTeams();

	// all teams
	public Iterable<String> teams();

	// number of wins for given team
	public int wins(String team);

	// number of losses for given team
	public int losses(String team);

	// number of remaining games for given team
	public int remaining(String team);

	// number of remaining games between team1 and team2
	public int against(String team1, String team2);

	// is given team eliminated?
	public boolean isEliminated(String team);

	// subset R of teams that eliminates given team; null if not eliminated
	public Iterable<String> certificateOfElimination(String team);

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
