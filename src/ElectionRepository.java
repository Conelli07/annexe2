import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElectionRepository {

    private final Connection connection;

    public ElectionRepository(Connection connection) {
        this.connection = connection;
    }

    public long countAllVotes() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_votes FROM vote";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("total_votes");
            if (rs.next()) {
                long count = rs.getLong("total_votes");
                System.out.println(count);
                return count;
            }
        }
        return 0;
    }

    public List<VoteTypeCount> countVotesByType() throws SQLException {
        String sql = """
                SELECT vote_type, COUNT(*) AS count
                FROM vote
                GROUP BY vote_type
                ORDER BY vote_type
                """;

        List<VoteTypeCount> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String voteType = rs.getString("vote_type");
                long count = rs.getLong("count");
                System.out.println(voteType + " | " + count);
                results.add(new VoteTypeCount(voteType, count));
            }
        }
        return results;
    }

    public List<CandidateVoteCount> countValidVotesByCandidate() throws SQLException {
        String sql = """
                SELECT c.name AS candidate_name,
                       COUNT(v.id) AS valid_vote
                FROM candidate c
                LEFT JOIN vote v
                    ON v.candidate_id = c.id
                   AND v.vote_type = 'VALID'
                GROUP BY c.name
                ORDER BY c.name
                """;

        List<CandidateVoteCount> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("candidate_name | valid_vote");
            while (rs.next()) {
                String name = rs.getString("candidate_name");
                long count = rs.getLong("valid_vote");
                System.out.printf("%-15s| %d%n", name, count);
                results.add(new CandidateVoteCount(name, count));
            }
        }
        return results;
    }

    public VoteSummary computeVoteSummary() throws SQLException {
        String sql = """
                SELECT
                    COUNT(CASE WHEN vote_type = 'VALID' THEN 1 END) AS valid_count,
                    COUNT(CASE WHEN vote_type = 'BLANK' THEN 1 END) AS blank_count,
                    COUNT(CASE WHEN vote_type = 'NULL'  THEN 1 END) AS null_count
                FROM vote
                """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("valid_count | blank_count | null_count");
            if (rs.next()) {
                long valid = rs.getLong("valid_count");
                long blank = rs.getLong("blank_count");
                long nullV = rs.getLong("null_count");
                System.out.println(valid + "           | " + blank + "           | " + nullV);
                return new VoteSummary(valid, blank, nullV);
            }
        }
        return new VoteSummary(0, 0, 0);
    }

    public double computeTurnoutRate() throws SQLException {
        String sqlVoters = "SELECT COUNT(*) AS total FROM voter";
        String sqlVotes  = "SELECT COUNT(DISTINCT voter_id) AS voted FROM vote";

        long totalVoters = 0;
        long totalVoted  = 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlVoters)) {
            if (rs.next()) totalVoters = rs.getLong("total");
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlVotes)) {
            if (rs.next()) totalVoted = rs.getLong("voted");
        }

        if (totalVoters == 0) return 0.0;
        return (double) totalVoted / totalVoters * 100.0;
    }

    public ElectionResult findWinner() throws SQLException {
        String sql = """
                SELECT c.name AS candidate_name,
                       COUNT(v.id) AS valid_vote_count
                FROM candidate c
                JOIN vote v
                    ON v.candidate_id = c.id
                   AND v.vote_type = 'VALID'
                GROUP BY c.name
                ORDER BY valid_vote_count DESC
                LIMIT 1
                """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("candidate_name | valid_vote_count");
            if (rs.next()) {
                String name  = rs.getString("candidate_name");
                long   count = rs.getLong("valid_vote_count");
                System.out.printf("%-15s| %d%n", name, count);
                return new ElectionResult(name, count);
            }
        }
        return null;
    }
}