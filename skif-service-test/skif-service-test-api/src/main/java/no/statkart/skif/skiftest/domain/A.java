package no.statkart.skif.skiftest.domain;


/**
 * @author Henrik Fredholm
 */
public class A {
    private String text;

    public A() {
    }

    public A(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}