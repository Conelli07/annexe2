public record VoteTypeCount(String voteType, long count) {
    @Override
    public String toString() {
        return "VoteTypeCount(voteType=" + voteType + ", count=" + count + ")";
    }
}