package no.statkart.skif.skiftest.service.test.tutorial;

public class MyException extends RuntimeException {
    private A a; private B b;
    public MyException(String s, A a, B b) {
        super(s);
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }
}
