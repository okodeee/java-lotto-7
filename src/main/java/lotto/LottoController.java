package lotto;

import camp.nextstep.edu.missionutils.*;
import java.util.*;
import java.util.stream.Collectors;

public class LottoController {
    public void run() {
        // 로또 구입 금액 입력 받기
        int inputCash = getValidatedInputCash();
        int lottoCount = calculateLottoCount(inputCash);

        // 로또 생성 과정
        List<Lotto> lottos = getLottos(lottoCount);

        // 발행한 로또 수량 및 번호 출력
        LottoView.printLottos(lottoCount, lottos);

        // 당첨 번호 및 보너스 번호 입력 받기
        Lotto winningNumbers = getWinningNumbers();

        // 보너스 번호 입력 받기
        Integer bonusNumber = getBonusNumber(winningNumbers);

        // 당첨 여부 확인
        // 등수 계산
        Map<LottoRank, Integer> rankCount = new EnumMap<>(LottoRank.class);
        int totalPrize = 0;

        totalPrize = getTotalPrize(rankCount, lottos, winningNumbers, bonusNumber, totalPrize);

        // 당첨 내역 출력
        LottoView.printLottoResult(rankCount);

        // 수익률 계산 및 출력
        LottoView.printReturnRate(totalPrize, inputCash);
    }

    private int getValidatedInputCash() {
        while (true) {
            try {
                int inputCash = LottoView.getInputCash();
                validateInputCash(inputCash);
                return inputCash;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] 잘못된 입력입니다. 정수를 입력하세요.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void validateInputCash(int inputCash) {
        if (inputCash < 1000) {
            throw new IllegalArgumentException("[ERROR] 1000원 이상으로 입력해주세요.");
        } else if (inputCash % 1000 != 0) {
            throw new IllegalArgumentException("[ERROR] 1000원 단위로 입력해주세요.");
        }
    }

    private int calculateLottoCount(int inputCash) {
        return inputCash / 1000;
    }

    private static List<Lotto> getLottos(int lottoCount) {
        List<Lotto> lottos = new ArrayList<>();
        for (int i = 0; i < lottoCount; i++) {
            lottos.add(new Lotto(Randoms.pickUniqueNumbersInRange(1, 45, 6)));
        }
        return lottos;
    }

    private Lotto getWinningNumbers() {
        Lotto winningNumbers = null;
        while (true) {
            try {
                String inputNumbers = LottoView.getWinningNumbers();
                winningNumbers = new Lotto(parseWinningNumbers(inputNumbers));
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return winningNumbers;
    }

    private List<Integer> parseWinningNumbers(String inputNumbers) {
        try {
            return Arrays.stream(inputNumbers.replace(" ", "").split(",", -1))
                    .map(Integer::parseInt)
                    .peek(number -> {
                        if (number < 1 || number > 45) {
                            throw new IllegalArgumentException("[ERROR] 로또 번호는 1부터 45 사이의 숫자여야 합니다.");
                        }
                    })
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("[ERROR] 잘못된 입력입니다. 정수를 입력하세요.");
        }
    }

    private Integer getBonusNumber(Lotto winningNumbers) {
        Integer bonusNumber;
        while (true) {
            try {
                bonusNumber = validateBonusNumber(winningNumbers.getNumbers(), LottoView.getBonusNumber());
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return bonusNumber;
    }

    private Integer validateBonusNumber(List<Integer> winningNumbers, String inputNumber) {
        try {
            Integer bonusNumber = Integer.parseInt(inputNumber);
            if (bonusNumber < 1 || bonusNumber > 45) {
                throw new IllegalArgumentException("[ERROR] 로또 번호는 1부터 45 사이의 숫자여야 합니다.");
            } else if (winningNumbers.contains(bonusNumber)) {
                throw new IllegalArgumentException("[ERROR] 보너스 번호는 당첨 번호와 중복될 수 없습니다.");
            }
            return bonusNumber;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("[ERROR] 잘못된 입력입니다. 정수를 입력하세요.");
        }
    }

    private static int getTotalPrize(Map<LottoRank, Integer> rankCount, List<Lotto> lottos, Lotto winningNumbers,
                                     Integer bonusNumber, int totalPrize) {
        // 각 등수의 개수를 0으로 초기화
        for (LottoRank rank : LottoRank.values()) {
            rankCount.put(rank, 0);
        }

        for (Lotto lotto : lottos) {
            Set<Integer> matchedNumbers = new HashSet<>(winningNumbers.getNumbers());
            Set<Integer> lottoNumbers = new HashSet<>(lotto.getNumbers());

            boolean isBonusMatched = lottoNumbers.contains(bonusNumber);
            matchedNumbers.retainAll(lottoNumbers);

            int matchCount = matchedNumbers.size();
            LottoRank rank = LottoRank.calculateRank(matchCount, isBonusMatched);

            if (rank != null) {
                rankCount.put(rank, rankCount.get(rank) + 1);
                totalPrize += rank.getPrize();
            }
        }
        return totalPrize;
    }
}
