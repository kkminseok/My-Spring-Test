import com.my.compareEntity.IntObj;
import com.my.compareEntity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;

public class QualityTest {

    @Test
    @DisplayName("조건 연산자 가변 인수 호출")
    void testConditionalOperatorVarargs() {
        printFormatted("Hello %s%n", "admin");
    }
    private void printFormatted(String format, Object... args) {
//        if(args.length == 0) {
//            System.out.printf(format, "user");
//        } else {
//            System.out.printf(format, args);
//        }

        System.out.printf(format, args.length == 0 ? "user" : args);
    }

    @Test
    @DisplayName("잘못된 메서드 참조 사용")
    void givenWrongMethodReference() {
        List<Integer> list = Arrays.asList(0, -3, 3, -1, 1, 2);
        list.sort(Integer::max);
        // [0, -3 , 3, -1, 1, 2]
        System.out.println(list);
    }

    @Test
    @DisplayName("정수 나눗셈 중 반올림 문제")
    void testIntegerDivisionRoundingIssue() {
        Assertions.assertEquals(process(2), process(3));
    }

    private double process(int value) {
        double half = value / 2;
        return half;
    }

    @Test
    @DisplayName("Integer Min Value특수성")
    void testIntegerMinValueSpecialCase() {
        Assertions.assertEquals(Math.abs(Integer.MIN_VALUE), Integer.MIN_VALUE);
    }

    @Test
    @DisplayName("Double 최솟값 확인")
    void testDoubleMinValue() {
        System.out.println("Double.MIN_VALUE: " + Double.MIN_VALUE);
        System.out.println("-Double.MAX_VALUE" + (-Double.MAX_VALUE));
        Assertions.assertEquals(Double.MIN_VALUE, 4.9E-324);
    }

    @Test
    @DisplayName("replaceAll 메서드 실수 테스트")
    void testReplaceAllMethodMistake() {
        String input = "$0minseok";
        String template = "Hello USER_NAME";
        String result = template.replaceAll("USER_NAME", input);
        Assertions.assertEquals("Hello USER_NAMEminseok", result);
    }

    @Test
    @DisplayName("indexOf 메서드 실수 테스트")
    void testIndexOfMethodMistake() {
        String input = """
                ]AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAAAAA
                AAAAAAA[AA
                v
                """;
        int start = input.indexOf('[');
        int end = input.indexOf(start, ']');
        Assertions.assertEquals(118, start);
        Assertions.assertEquals(122,end);
    }


    @Test
    @DisplayName("BigInteger 비교 특이점 테스트")
    void testBigIntegerComparison() {
        BigDecimal x = new BigDecimal("0");
        BigDecimal y = new BigDecimal("0.0");

        Assertions.assertNotEquals(x, y);
    }

    @Test
    @DisplayName("63 compare() 문제")
    void compareMistakeTest() {
        User u1 = new User("Mary", 30);
        User u2 = new User("Mary", 20);
        User u3 = new User("Joe", 30);
        Assertions.assertEquals(1, u1.compareTo(u2));
        Assertions.assertEquals(3, u1.compareTo(u3));
    }

    @Test
    @DisplayName("65 숫자뺄셈 compare()문제")
    void compareMinusMistakeTest() {
        IntObj x = new IntObj(2_000_000_000);
        IntObj y = new IntObj(0);
        IntObj z = new IntObj(-2_000_000_000);

        Assertions.assertTrue(x.compareTo(y) > 0);
        Assertions.assertTrue(y.compareTo(z) > 0);
        Assertions.assertFalse(x.compareTo(z) > 0);
    }

    @Test
    @DisplayName("69 관련 없는 객체 타입 검색")
    void unrelatedObjectTypeSearchTest() throws IOException {
        Set<File> allowedFiles = new HashSet<>();

        allowedFiles.contains(File.createTempFile("text", ".txt"));

        List<Short> shortList = new ArrayList<>();
        short s = 1;
        shortList.contains(s + 1 );
    }

    @Test
    @DisplayName("70 단일 객체와 컬렉션 혼합")
    void singleObjectAndCollectionMixTest() {
        List<Object> list = new ArrayList<>();

        List<String> list2 = List.of("my", "name", "is");
        list.add("one");
        list.add("two");
        list.add("three");


        list.add(list2);
        list.stream().forEach(System.out::println);
    }
}
