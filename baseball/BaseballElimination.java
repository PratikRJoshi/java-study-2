/**
 * Determine which teams in an MLB division are mathematically eliminated from
 * wining the division by the end of the regular season.
 * <p>
 * Usage: java BaseballElimination inputfile.txt
 *
 * @author William Schwartz
 */
public class BaseballElimination {
	// create a baseball division from given filename in format specified below
	public BaseballElimination(String filename);

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
