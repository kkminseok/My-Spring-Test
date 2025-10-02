import com.my.chapter10.MethodChainingOrderBuilder;
import com.my.chapter10.NestedFunctionOrderBuilder;
import com.my.chapter10.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Chapter10 {

    @Test
    @DisplayName("빌더 패턴 테스트")
    void builderPatternTest() {
        Order order = MethodChainingOrderBuilder.forCustomer("minseok")
                .buy(100)
                .stock("IBM")
                .on("NYSE")
                .at(125.00)
                .sell(50)
                .stock("GOOGLE")
                .on("NASDAQ")
                .at(375.00)
                .end();
        System.out.println(order);
    }

    @Test
    @DisplayName("중첩 함수 테스트" )
    void nestedFunctionTest() {
        Order order = NestedFunctionOrderBuilder.order("minseok",
                NestedFunctionOrderBuilder.buy(100,
                        NestedFunctionOrderBuilder.stock("IBM", "NYSE"),
                        NestedFunctionOrderBuilder.at(125.00)),
                NestedFunctionOrderBuilder.sell(50,
                        NestedFunctionOrderBuilder.stock("GOOGLE", "NASDAQ"),
                        NestedFunctionOrderBuilder.at(375.00))
        );

        System.out.println(order);

    }
}
