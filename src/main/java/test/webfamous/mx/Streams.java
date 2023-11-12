package test.webfamous.mx;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static test.webfamous.mx.ReferenceOperator.getList;

public class Streams {
    public static void main(String[] args) {
        List<String> courseList = getList("Erix", "Juan", "Robert");
        for (String name : courseList) {
            System.out.println(name);
        }

        System.out.println("//////////////////////////");

        Stream<String> namesStream = Stream.of("Erix", "Robert", "Ferchis");
        Stream<String> uppercaseNamesStream = namesStream.map( (text) -> text.toUpperCase(Locale.ROOT));
        uppercaseNamesStream.forEach(System.out::println);

        System.out.println("//////////////////////////");

        Stream<String> names2 = Stream.of("Erix", "Robert", "Ferchis", "Juan", "Robert");
        names2.map( name -> name.toUpperCase(Locale.ROOT) + "!!")
                .filter( name -> name.contains("ERIX"))
                .forEach(System.out::println);


        System.out.println("//////////////////////////");

        long startTime = System.nanoTime();

        IntStream intStream = IntStream.iterate(0, x -> x + 1);
        intStream.limit(1_000_000)
                //.parallel()
                .filter( x -> x % 2 == 0)
                .forEach(System.out::println);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        long durationInMs = duration / 1_000_000;
        System.out.println("Tiempo de ejecución en nanosegundos: " + duration);
        System.out.println("Tiempo de ejecución en milisegundos: " + durationInMs);

    }



}
