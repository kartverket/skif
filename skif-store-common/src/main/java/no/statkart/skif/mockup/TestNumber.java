package no.statkart.skif.mockup;

import java.io.Serializable;

/**
 * Identifiserer et testsett. Testsett 0 skal aldri modifiseres.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class TestNumber implements Serializable
{
    public static final TestNumber NR_0 = new TestNumber(0);

    private final int number;

    public TestNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Genererer prefiks for id basert på testnummer.
     *
     * @return id-prefiks unikt for testsett
     */
    public long getPrefix() {
        return getNumber() + 20000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestNumber that = (TestNumber) o;

        return number == that.number;

    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public String toString() {
        return "TestNumber{" +
                "number=" + number +
                '}';
    }
}
