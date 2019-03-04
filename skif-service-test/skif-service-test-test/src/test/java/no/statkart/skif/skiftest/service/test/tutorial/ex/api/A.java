package no.statkart.skif.skiftest.service.test.tutorial.ex.api;

import java.util.Objects;

public class A {
    private int x;

    public A() {
    }

    public A(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public A setX(int x) {
        this.x = x;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        A a = (A) o;
        return x == a.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x);
    }
}
