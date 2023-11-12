package test.webfamous.mx;

import java.util.Optional;

public class Optionals {

    public static void main(String[] args) {
        Optional<String> value = getSomeString();
        value.ifPresent( (text) -> {
            System.out.println(text);
        });

        String anotherText = value.orElseGet( () -> "Nothing");
        System.out.println(anotherText);
    }

    private static Optional<String> getSomeString() {
        return Optional.ofNullable(null);

    }
}
