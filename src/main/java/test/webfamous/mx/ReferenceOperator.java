package test.webfamous.mx;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ReferenceOperator {

    public static void main(String[] args) {
        List<String> list = getList("Erix", "Juan", "Robert");
        //Consumer<String> printer = text -> System.out.println(text);
        list.forEach(System.out::println);
        list.forEach( value -> System.out.println(value));

        useZero(() -> 3);

        useBiFunction((a, b) -> a * b);
        useBiFunction((a, b) -> {
            System.out.println("Hello from Lambda");
            return a * b;
        });

        useBiFunction((Integer a, Integer b) -> a * b);

    }
    static <T> List<T> getList(T... elements) {
        return Arrays.asList(elements);
    }

    static void useZero(ZeroArguments zeroArguments) {
        // TODO: Something
    }

    static void useBiFunction(BiFunction<Integer, Integer, Integer> biFunction) {
        System.out.println("From biFunction ");
    }

    @FunctionalInterface
    interface ZeroArguments {
        int get();
    }
}
