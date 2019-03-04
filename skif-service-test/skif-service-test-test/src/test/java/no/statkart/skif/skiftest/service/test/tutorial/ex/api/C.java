package no.statkart.skif.skiftest.service.test.tutorial.ex.api;

import java.util.Objects;

public class C {
    private int sum, broek;

    public C() {}

    public C(int sum, int broek) {
        this.sum = sum;
        this.broek = broek;
    }

    public int getSum() {
        return sum;
    }

    public C setSum(int sum) {
        this.sum = sum;
        return this;
    }

    public int getBroek() {
        return broek;
    }

    public C setBroek(int broek) {
        this.broek = broek;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        C c = (C) o;
        return sum == c.sum &&
                broek == c.broek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum, broek);
    }
}
