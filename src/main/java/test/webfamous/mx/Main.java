package test.webfamous.mx;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.function.Function;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Function<Integer, Boolean> function = new Function<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer integer) {
                return integer == 4;
            }
        };

        Function<Integer, Boolean> function1 = x -> x == 4;


        TriFunctions<Integer, Integer, Integer, LocalDate> parseDate =
                (day, month, year) ->
                        LocalDate.parse(year + "-" + month + "-" + day);


        TriFunctions<Integer, Integer, Integer, Integer> calculateAge =
                (day, month, year) ->
                        Period.between(parseDate.apply(day, month, year), LocalDate.now()).getYears();

        System.out.println(calculateAge.apply(13, 10, 1988));
    }

    @FunctionalInterface
    interface TriFunctions<T, U, V, R> {
        R apply(T t, U u, V v);
    }

}