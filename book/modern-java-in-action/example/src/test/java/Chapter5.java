import com.my.Dish;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Chapter5 {

    List<Dish> specialMenu = Arrays.asList(
            new Dish("seasonal fruit", true, 120, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER));

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
    void takeWhileTest() {
        List<Dish> slicedMenu2 = specialMenu.stream()
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(toList());

        slicedMenu2.stream().forEach(System.out::println);

    }

    @Test
    void dropWhileTest() {
        List<Dish> slicedMenu3 = specialMenu.stream()
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(toList());

        slicedMenu3.stream().forEach(System.out::println);
    }

    @Test
    void quiz1() {
        List<Dish> result = menu.stream()
                .filter(dish -> dish.getType().equals(Dish.Type.MEAT))
                .limit(2)
                .collect(toList());

        result.stream().forEach(System.out::println);
    }

    @Test
    void quiz2() {
        List<Integer> input = List.of(1, 2, 3, 4, 5);
        List<Integer> result = input.stream()
                .map(i -> i * i)
                .collect(toList());
        result.stream().forEach(System.out::println);

        List<Integer> input1 = List.of(1, 2, 3);
        List<Integer> input2 = List.of(3, 4);

        List<int[]> result2 = input1.stream()
                .flatMap(i -> input2.stream()
                        .map(j -> new int[]{i, j}))
                .collect(toList());

        result2.stream().forEach(i -> {
            System.out.println(i[0] + " "  + i[1]);
        });

        List<int[]> result3 = input1.stream()
                .flatMap(i -> input2.stream()
                        .filter(j -> (i + j) % 3 == 0)
                        .map(j -> new int[]{i, j}))
                .collect(toList());
        result3.stream().forEach(i -> {
            System.out.println("(" + i[0] + " "  + i[1] + ")");
        });



    }

}
