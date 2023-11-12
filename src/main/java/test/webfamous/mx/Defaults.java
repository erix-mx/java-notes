package test.webfamous.mx;

public class Defaults {

    @FunctionalInterface
    interface StringOperator {
        int getAmount();

        default void operate(String text) {
            int x = getAmount();
            while (x-- > 0) {
                System.out.println(text);
            }
        }

        default Integer getCurrentNumber() {
            return getAmount();
        }
    }

    @FunctionalInterface
    interface DoOperation {
        void take(String text);
        default void execute(int times, String text) {
            while (times-- > 0) {
                System.out.println(text);
            }
        }
    }

    public static void main(String[] args) {
        StringOperator stringOperator = () -> 5;
        stringOperator.operate("Erix");
        System.out.println(stringOperator.getCurrentNumber());

        System.out.println("/////////////////////////////////");

        DoOperation doOperation = text -> System.out.println("🦀 " + text);
        doOperation.execute(10, "Erix");
    }

}
