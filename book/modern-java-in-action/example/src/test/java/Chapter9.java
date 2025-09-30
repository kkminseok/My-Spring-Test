import com.my.chapter9.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Chapter9 {

    @Test
    @DisplayName("람다 테스팅")
    void lambdaTesting() {
        Point p1 = new Point(10, 15);
        Point p2 = new Point(10,20);
        int result = Point.compareByXAndThenY.compare(p1, p2);
        assertTrue(result < 0);
    }

    @Test
    @DisplayName("람다 내부 표현식 검증 테스트")
    void lambdaInternalExpressionTesting() {
        List<Point> points = Arrays.asList(new Point(5,5), new Point(10,10));
        List<Point> expected = Arrays.asList(new Point(15,5), new Point(20,10));

        List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
    }

    @Test
    @DisplayName("람다 스택 트레이스 확인")
    void lambdaStackTraceTesting() {
        //의도적으로 람다 에러 발생
        List<Point> points = Arrays.asList(new Point(1,2), null);
        points.stream().map(p -> p.getX() + p.getY()).forEach(System.out::println);

    }
}
