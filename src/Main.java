import java.sql.*;
import java.util.List;

public class Main {

    private static final String URL      = "jdbc:postgresql://localhost:5432/election_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) throws Exception {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            ElectionRepository repo = new ElectionRepository(conn);

            System.out.println("=== Q1 - Nombre total de votes ===");
            long totalVote = repo.countAllVotes();
            System.out.println("totalVote=" + totalVote);

            System.out.println();

            System.out.println("=== Q2 - Votes par type ===");
            List<VoteTypeCount> byType = repo.countVotesByType();
            System.out.println(byType);

            System.out.println();

            System.out.println("=== Q3 - Votes valides par candidat ===");
            List<CandidateVoteCount> byCandidate = repo.countValidVotesByCandidate();
            System.out.println(byCandidate);

            System.out.println();

            System.out.println("=== Q4 - Synthèse globale ===");
            VoteSummary summary = repo.computeVoteSummary();
            System.out.println(summary);

            System.out.println();

            System.out.println("=== Q5 - Taux de participation ===");
            double turnout = repo.computeTurnoutRate();
            System.out.printf("turnoutRate=%.0f%%%n", turnout);

            System.out.println();

            System.out.println("=== Q6 - Résultat élection ===");
            ElectionResult winner = repo.findWinner();
            System.out.println(winner);
        }
    }
}