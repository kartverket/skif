package no.statkart.skif.skiftest.service.test.tutorial;

import java.util.Objects;

class C {
    int sum, broek;

    C(int sum, int broek) {
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
