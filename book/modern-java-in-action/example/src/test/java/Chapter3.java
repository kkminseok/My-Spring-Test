import com.my.chapter3.BufferedReaderProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public class Chapter3 {

    @Test
    @DisplayName("3.3 람다 활용 실행 어라운드 패턴")
    void processTest() {
        try {
            System.out.println(processFile());
        } catch (IOException e) {
            System.out.println("에러: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3.3 람다 활용 실행 어라운드 패턴 - 함수형 인터페이스")
    void processFunctionalInterfaceTest() {
        try {
            System.out.println(processFile(new BufferedReaderProcessor() {
                @Override
                public String process(BufferedReader reader) throws IOException {
                    return reader.readLine();
                }
            }));
        } catch (IOException e) {
            System.out.println("에러: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3.3 람다 활용 실행 어라운드 패턴 - 람다")
    void processLambdaTest() {
        try {
            System.out.println(processFile((BufferedReader br) -> br.readLine()));
        } catch (IOException e) {
            System.out.println("에러: " + e.getMessage());
        }
    }

    private String processFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/nhn/Documents/my/baeldung-test/book/modern-java-in-action/example/src/test/java/data.txt"))) {
            return br.readLine();
        }
    }

    private String processFile(BufferedReaderProcessor reader) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/nhn/Documents/my/baeldung-test/book/modern-java-in-action/example/src/test/java/data.txt"))) {
            return reader.process(br);
        }
    }

    @Test
    @DisplayName("3.4 Predicate Example")
    void predicateExample() {
        Predicate<String> isEmpty = (String s) -> s.isEmpty();

        List<String> name = List.of("Alice", "Bob", "Charlie", "", "David", "");
        List<String> filter = filter(name, isEmpty);
        System.out.println(filter);
    }

    private <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (!predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    @Test
    @DisplayName("3.4 Consumer Example")
    void consumerTest() {
        List<String> name = List.of("Alice", "Bob", "Charlie", "David");
        Consumer<String> consumer = (String s) -> System.out.println(s);
        forEach(name, consumer);
        forEach(name, (String s) -> System.out.println(s));
    }

    private <T> void forEach(List<T> list, Consumer<T> consumer) {
        for (T item : list) {
            consumer.accept(item);
        }
    }

    @Test
    @DisplayName("3.4 Function Example")
    void functionTest() {
        List<String> names = List.of("Alice", "Bob", "Charlie", "David");
        Function<String, Integer> function = (String s) -> s.length();
        List<Integer> lengths = map(names, function);
        lengths.stream().forEach(System.out::println);
    }

    private <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(function.apply(item));
        }
        return result;
    }

    @Test
    @DisplayName("3.5 람다 지역변수 테스트")
    void lambdaLocalVariableTest() {
        int num = 1;
        Function<Integer, Integer> function = (Integer i) -> i + num;
        System.out.println(function.apply(2));
    }

    @Test
    @DisplayName("3.6 메서드 참조 테스트")
    void methodReferenceTest() {
        ToIntFunction<String> stringIntegerFunction = (String s) -> Integer.parseInt(s);
        BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);
        Predicate<String> startsWithNumber = (String string) -> this.startsWithNumber(string);

        // 메서드 참조로 변환
        ToIntFunction<String> stringIntegerFunctionMethodReference = Integer::parseInt;
        BiPredicate<List<String>, String> containsMethodReference = List::contains;
        Predicate<String> startsWithNumberMethodReference = this::startsWithNumber;
    }

    @Test
    @DisplayName("3.6 메서드 참조 인자 3개 경우")
    void methodReferenceThreeArgTest() {
        TriFunction<Integer, String, Integer, User> userCreator = (id, name, weight) -> new User(id, name, weight);
        User minseok = userCreator.apply(1, "minseok", 75);

        TriFunction<Integer, String, Integer, User> userCreatorMethodRef = User::new;
        User minseok2 = userCreatorMethodRef.apply(2, "minseok2", 80);
        Assertions.assertEquals(75, minseok.weight);
        Assertions.assertEquals(2, minseok2.id);

    }

    private boolean startsWithNumber(String string) {
        return string != null && !string.isEmpty() && Character.isDigit(string.charAt(0));
    }

    public record User(int id, String name, int weight) {

    }

    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }


}
