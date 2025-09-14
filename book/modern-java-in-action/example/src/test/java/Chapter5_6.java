import com.my.Trader;
import com.my.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

public class Chapter5_6 {


    Trader raoul = new Trader("Raoul", "Cambridge");
    Trader mario = new Trader("Mario", "Milan");
    Trader alan = new Trader("Alan", "Cambridge");
    Trader brian = new Trader("Brian", "Cambridge");


    List<Transaction> transactions = Arrays.asList(
            new Transaction(brian, 2011, 300),
            new Transaction(raoul, 2012, 1000),
            new Transaction(raoul, 2011, 400),
            new Transaction(mario, 2012, 710),
            new Transaction(mario, 2012, 700),
            new Transaction(alan, 2012, 950)
    );

    @Test
    void quiz5_6_1() {
        // 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리
        List<Transaction> quiz1 = transactions
                .stream()
                .filter(ta -> ta.getYear() == 2011)
                .sorted(comparing(ta -> ta.getValue()))
                .collect(toList());
        quiz1.stream()
                .forEach(System.out::println);
    }

    @Test
    void quiz5_6_2() {
        // 거래자가 근무하는 모든 도시를 중복없이 나열
        List<String> result = transactions
                .stream()
                .map(ta -> ta.getTrader().getCity())
                .distinct()
                .collect(toList());
        result.stream().forEach(System.out::println);
    }

    @Test
    void quiz5_6_3() {
        //케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
        List<String> result = transactions
                .stream()
                .filter(ta -> ta.getTrader().getCity().equals("Cambridge"))
                .map(ta -> ta.getTrader().getName())
                .distinct()
                .sorted((o1, o2) -> o1.compareTo(o2))
                .collect(toList());
        result.stream().forEach(System.out::println);
    }

    @Test
    void quiz5_6_4() {
        // 모든 거래자의 이름을 알파벳순으로 정렬해서 반환
        String result = transactions
                .stream()
                .map(Transaction::getTrader)
                .map(Trader::getName)
                .distinct()
                .sorted()
                .reduce(" ", (a, b) -> a + b);
        System.out.println(result);
    }

    @Test
    void quiz5_6_5() {
        //밀라노에 거래자가 있는가?
        transactions
                .stream()
                .filter(ta -> ta.getTrader().getCity().equals("Milan"))
                .findAny()
                .ifPresent((s) -> System.out.println("찾음"));

        boolean isMilan = transactions
                .stream()
                .anyMatch(ta -> ta.getTrader().getCity().equals("Milan"));
        System.out.println(isMilan);
    }

    @Test
    void quiz5_6_6() {
        //케임브리지에 거주하는 거래자의 모든 트랜잭션 값 출력
        transactions
                .stream()
                .filter(ta -> ta.getTrader().getCity().equals("Cambridge"))
                .map(ta -> ta.getValue())
                .forEach(System.out::println);
    }

    @Test
    void quiz5_6_7() {
        // 전체 트랜잭션 중 최댓값은 얼마인가?
        Optional<Integer> maxValue = transactions
                .stream()
                .map(Transaction::getValue)
                .reduce(Integer::max);
        System.out.println(maxValue);
    }

    @Test
    void quiz5_6_8() {
        // 전체 트랜잭션 중 최솟값은 얼마인가?
        Optional<Integer> minValue = transactions
                .stream()
                .map(Transaction::getValue)
                .reduce(Integer::min);
        System.out.println(minValue);
    }

    @Test
    void fibonacci() {
        //피보나치 수열 20개 iterate사용
        Stream.iterate(new int[]{0,1}, t -> new int[]{t[1],t[0] + t[1] })
                .limit(20)
                .forEach(t-> System.out.println("(" + t[0] +"," +t[1] +")"));

    }
}
