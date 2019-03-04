package no.statkart.skif.skiftest.service.test.tutorial.ex.api;

import java.util.Objects;

public class B {
    private int y;

    public B() {}
    public B(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public B setY(int y) {
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        B b = (B) o;
        return y == b.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y);
    }
}
