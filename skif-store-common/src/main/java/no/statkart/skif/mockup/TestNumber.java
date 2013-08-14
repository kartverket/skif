package no.statkart.skif.mockup;

import java.io.Serializable;

/**
 * Identifiserer et testsett. Testsett 0 skal aldri modifiseres.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
public class TestNumber implements Serializable {
    private int offset;
    private int number;


    @Deprecated // WS-mapping
    public TestNumber() {
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Deprecated // WS-mapping
    public void setNumber(int number) {
        this.number = number;
    }

    public TestNumber(int offset, int number) {
        this.offset = offset;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean isNR_0() {
        return number==0;
    }

    /**
     * Genererer prefiks for id basert på testnummer.
     *
     * @return id-prefiks unikt for testsett
     */
    public long getPrefix() {
        return offset + number;
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
