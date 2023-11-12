package test.webfamous.mx;

public class Chaining {

    public static void main(String[] args) {
        Chainer chainer = new Chainer();
        chainer.sayBye().sayBye().sayHi();
    }

    static class Chainer {
        Chainer sayHi() {
            System.out.println("Say Hi!");
            return this;
        }

        Chainer sayBye() {
            System.out.println("Say Bye");
            return this;
        }
    }
}
