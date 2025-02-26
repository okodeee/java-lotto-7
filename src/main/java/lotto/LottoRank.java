package lotto;

enum LottoRank {
    FIFTH(3, 5_000),
    FOURTH(4, 50_000),
    THIRD(5, 1_500_000),
    SECOND(5, 30_000_000),
    FIRST(6, 2_000_000_000);

    private final int matchCount;
    private final int prize;

    LottoRank(int matchCount, int prize) {
        this.matchCount = matchCount;
        this.prize = prize;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public int getPrize() {
        return prize;
    }

    public static LottoRank calculateRank(int matchCount, boolean isBonusMatched) {
        if (matchCount == 6) {
            return LottoRank.FIRST;
        } else if (matchCount == 5 && isBonusMatched) {
            return LottoRank.SECOND;
        } else if (matchCount == 5) {
            return LottoRank.THIRD;
        } else if (matchCount == 4) {
            return LottoRank.FOURTH;
        } else if (matchCount == 3) {
            return LottoRank.FIFTH;
        }
        return null;  // 당첨되지 않은 경우
    }
}