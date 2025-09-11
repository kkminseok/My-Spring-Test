import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CollectionsTest {

    @Test
    void convertArrayToList() {
        int[] array = {1, 2, 3, 4, 5};
        // int[] -> List<Integer> 변환
        List<Integer> datas = Arrays.stream(array)
                .boxed()
                .collect(Collectors.toList());
        //누적 합
        Arrays.parallelPrefix(array, (left, right) -> left + right);
        Arrays.stream(array).forEach(System.out::println);

        //바이너리 서치
        int i = Arrays.binarySearch(array, 15);
        Assertions.assertEquals(4, i);

    }

    @Test
    void streamTest() {
        int [] array = {1,2,3,4,5};
        IntStream intStream = Arrays.stream(array);
    }

    @Test
    void setTest() {
        //순서 보장 x
        Set<Integer> set = IntStream.range(0, 10).boxed().collect(Collectors.toSet());
        set.add(99);
        set.add(100);
        System.out.println(set);
        //순서 보장
        SortedSet<Integer> sortedSet = new TreeSet<>(set);

        System.out.println(sortedSet);
    }

    @Test
    void mapTest() {
        //순서 보장 x
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(89, "3");
        map.put(227, "3");
        map.put(0, "3");
        System.out.println(map);
        //순서 보장 o
        Map<Integer, String> map2 = new TreeMap<>(map);
        System.out.println(map2);
    }

    @Test
    @DisplayName("맵 정렬 테스트 (키)")
    void mapSortTestByKey() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "9");
        map.put(89, "5");
        map.put(227, "8");
        map.put(0, "4");
        map.put(4, "8");

        //사실 SortedMap을 쓰면됨 데이터 추가하면 오버헤드 존재
        SortedMap<Integer, String> sortedMap = new TreeMap<>(map);

        //Stream을 사용하여
        map.entrySet().stream().forEach(System.out::println);
        System.out.println("===================");

        // 값(Value) 정렬
        LinkedHashMap<Integer, String> sortedValueMap = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        sortedValueMap.entrySet().stream().forEach(System.out::println);
        System.out.println("===================");

        //키 정렬
        LinkedHashMap<Integer, String> sortedKeyMap = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        sortedKeyMap.entrySet().stream().forEach(System.out::println);



        //키, 값 정렬
        LinkedHashMap<Integer, String> sortedKeyValueMap = map.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, String>comparingByKey().reversed()
                        .thenComparing(Map.Entry.<Integer, String>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        sortedKeyValueMap.entrySet().stream().forEach(System.out::println);

    }
}
