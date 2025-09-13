import com.my.Dish;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

class Chapter4 {


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
    void getThreeHighCaloricDishNames() {
        List<String> threeHighCaloricDishNames =
                menu.stream()
                        .filter(dish -> dish.getCalories() > 300)
                        .map(Dish::getName)
                        .limit(3)
                        .collect(toList());
        threeHighCaloricDishNames.forEach(System.out::println);
    }

    @Test
    void quiz1() {
        List<String> highCaloricDishes = new ArrayList<>();
        Iterator<Dish> iterator = menu.iterator();
        while (iterator.hasNext()) {
            Dish dish = iterator.next();
            if(dish.getCalories() > 300) {
                highCaloricDishes.add(dish.getName());
            }
        }

        // 개선

        menu.stream()
                .filter(dish -> dish.getCalories()> 300)
                .map(Dish::getName)
                .collect(toList());
    }

    @Test
    void quiz2() {
        long count = menu
                .stream()
                .filter(d -> d.getCalories() > 300)
                .distinct()
                .limit(3)
                .count();
        // count는 최종연산
    }


}

