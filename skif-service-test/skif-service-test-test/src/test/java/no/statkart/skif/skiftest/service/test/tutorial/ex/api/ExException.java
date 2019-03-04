package no.statkart.skif.skiftest.service.test.tutorial.ex.api;

public class ExException extends RuntimeException {
    private A a;
    private B b;

    public ExException(String s, A a, B b) {
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
