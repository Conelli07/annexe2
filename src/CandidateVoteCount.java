public record CandidateVoteCount(String candidateName, long validVoteCount) {
    @Override
    public String toString() {
        return candidateName + "=" + validVoteCount;
    }
}