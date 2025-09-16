import com.my.Dish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Chapter6 {

    List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 400, Dish.Type.FISH),
            new Dish("salmon", false, 400, Dish.Type.FISH)
    );


    @Test
    @DisplayName("maxBy, minBy Test")
    void collectTestMaxMinBy() {
        Comparator<Dish> dishComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCaloriesDish = menu.stream()
                .collect(Collectors.maxBy(dishComparator));
        System.out.println(mostCaloriesDish.get());

        Optional<Dish> minCaloriesDish = menu.stream()
                .collect(Collectors.minBy(dishComparator));
        System.out.println(minCaloriesDish.get());
    }

    @Test
    @DisplayName("요약 연산")
    void summingIntTest() {
        Integer totalCalories = menu.stream()
                .collect(Collectors.summingInt(Dish::getCalories));
        System.out.println(totalCalories);

        Double avgCalories = menu.stream()
                .collect(Collectors.averagingInt(Dish::getCalories));
        System.out.println(avgCalories);

        IntSummaryStatistics summaryResult = menu.stream()
                .collect(Collectors.summarizingInt(Dish::getCalories));
        System.out.println(summaryResult);
    }

    @Test
    @DisplayName("문자열 연결")
    void joiningTest() {
        String shortMenu = menu.stream()
                .map(Dish::getName)
                .collect(Collectors.joining(", "));
        System.out.println(shortMenu);
    }

    @Test
    @DisplayName("범용 리듀싱 요약 연산")
    void reducingSummaryTest() {
        Integer totalCalories = menu.stream()
                .collect(Collectors.reducing(
                        0, Dish::getCalories, (i, j) -> i + j));
        System.out.println(totalCalories);
    }

    @Test
    @DisplayName("퀴즈 6-1")
    void quiz6_1() {
        String result1 = menu.stream()
                .map(Dish::getName)
                .collect(Collectors.reducing((s1, s2) -> s1 + s2)).get();

        System.out.println(result1);
    }

    @Test
    @DisplayName("그룹화 예제")
    void groupTutorial() {
        Map<Dish.Type, List<Dish>> dishesByType = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType));
        System.out.println(dishesByType);
    }

    @Test
    @DisplayName("레벨별 분류")
    void levelGroupTest() {
        Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream()
                .collect(Collectors.groupingBy(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                }));
        System.out.println(dishesByCaloricLevel);
    }

    @Test
    @DisplayName("그룹화된 요소 조작")
    void filteringGroupTest() {
        Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.filtering(dish -> dish.getCalories() > 500, Collectors.toList())));
        System.out.println(caloricDishesByType);

        Map<Dish.Type, List<String>> dishNamesByType = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType, Collectors.mapping(Dish::getName, Collectors.toList())));
        System.out.println(dishNamesByType);
    }

    @Test
    @DisplayName("다수준 그룹화")
    void multiGroupTest() {
        //1.
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.groupingBy(dish -> {
                            if (dish.getCalories() <= 400)
                                return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700)
                                return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        })));

        System.out.println(dishesByTypeCaloricLevel);

        //2.
        Map<Dish.Type, Long> typesCount = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.counting()));
        System.out.println(typesCount);

        //3.
        Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.maxBy(Comparator.comparing(Dish::getCalories))));
        System.out.println(mostCaloricByType);

        //4.
        Map<Dish.Type, Dish> mostCaloricByType2 = menu.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Dish::getCalories)),
                                Optional::get
                        )
                ));
        System.out.println(mostCaloricByType2);
    }

    @Test
    @DisplayName("분할함수 테스트")
    void partitionTest() {
        Map<Boolean, List<Dish>> partitionMenu = menu.stream()
                .collect(Collectors.partitioningBy(Dish::isVegetarian));
        System.out.println(partitionMenu);

        Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream()
                .collect(Collectors.partitioningBy(Dish::isVegetarian, Collectors.groupingBy(Dish::getType)));
        System.out.println(vegetarianDishesByType);

        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream()
                .collect(Collectors.partitioningBy(Dish::isVegetarian,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(Dish::getCalories)), Optional::get)));
        System.out.println(mostCaloricPartitionedByVegetarian);
    }

    @Test
    @DisplayName("퀴즈 6_2")
    void quiz6_2() {
        Map<Boolean, Map<Boolean, List<Dish>>> map1 = menu.stream()
                .collect(Collectors.partitioningBy(Dish::isVegetarian,
                        Collectors.partitioningBy(d -> d.getCalories() > 500)));
        System.out.println(map1);

        Map<Boolean, Long> map2 = menu.stream()
                .collect(Collectors.partitioningBy(Dish::isVegetarian,
                        Collectors.counting()));
        System.out.println(map2);
    }

    @Test
    @DisplayName("분할을 이용한 소수 판별 테스트")
    void primeTestByPartition() {
        System.out.println(partitionPrimes(4));
    }

    private Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(
                        Collectors.partitioningBy(candidate -> isPrime(candidate))
                );
    }

    private boolean isPrime(int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate %i == 0);
    }


    public enum CaloricLevel { DIET, NORMAL, FAT}
}
