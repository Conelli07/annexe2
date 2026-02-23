public record ElectionResult(String candidateName, long validVoteCount) {
    @Override
    public String toString() {
        return "ElectionResult(candidateName=" + candidateName +
                ", validVoteCount=" + validVoteCount + ")";
    }
}