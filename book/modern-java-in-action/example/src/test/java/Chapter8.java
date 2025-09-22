import com.my.Trader;
import com.my.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Chapter8 {

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
    @DisplayName("removeIf test")
    void removeIfTest() {
        transactions.removeIf(ta -> ta.getYear() == 2011);
    }

    @Test
    @DisplayName("replaceAll Test")
    void removeAllTest() {
        List<String> referenceCodes = List.of("a12", "C14", "b13");

        // 새 리스트로
        referenceCodes.stream()
                .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
                .collect(Collectors.toList())
                .forEach(System.out::println);

        referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
    }

    @Test
    @DisplayName("Map forEach Test")
    void mapForEachTest() {
        Map<Integer, String> integerStringMap = Map.of(13, "kms", 14, "kke");
        integerStringMap.forEach((key,value) -> System.out.println("age = " + key + " name = " + value));
    }

    @Test
    @DisplayName("Map sort example")
    void mapSortExample() {
        Map<String, String> favouriteMovies = Map.ofEntries(Map.entry("Raphael", "Star Wars"),
                Map.entry("Cristina", "Matrix"),
                Map.entry("Olivia", "James Bond"));

        favouriteMovies
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(System.out::println); // 사람의 이름을 알파벳 순으로 스트림 요소 처리
    }

    @Test
    @DisplayName("getOrDefault 메서드 테스트")
    void getOrDefaultTest() {
        Map<String, String> stringStringMap = Map.ofEntries(Map.entry("Raphael", "Star Wars"));

        System.out.println(stringStringMap.getOrDefault("test", "empty value!"));
    }

    @Test
    @DisplayName("computeIfAbsent 테스트")
    void computeIfAbsentTest() {
        Map<String, List<String>> stringStringMap = new HashMap<>();
        stringStringMap.computeIfAbsent("Raphael", name -> new ArrayList<>())
                .add("Star wars");

        stringStringMap.forEach((key, value) -> System.out.println(key + " = " + value));

        stringStringMap.remove("Raphael", List.of("Star wars"));
        stringStringMap.forEach((key, value) -> System.out.println(key + " = " + value));
    }

    @Test
    @DisplayName("Map replace 테스트")
    void replaceAllTest() {
        Map<String,String> favouriteMovies = new HashMap<>();
        favouriteMovies.put("Raphael", "Star Wars");
        favouriteMovies.put("Olivia", "james Bond");
        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);
    }

    @Test
    @DisplayName("Map merge 테스트")
    void mergeTest() {
        Map<String,String> family = Map.ofEntries(
                Map.entry("Teo", "Star Wars"),
                Map.entry("Cristina", "James Bond")
        );
        Map<String,String> friends = Map.ofEntries(
                Map.entry("Raphael", "Star Wars"),
                Map.entry("Cristina", "Matrix")
        );
        Map<String,String> everyOne = new HashMap<>(family);
        friends.forEach((k,v) ->
                everyOne.merge(k,v, (movie1, movie2) -> movie1 + " & " + movie2));
        System.out.println(everyOne);
    }


}
