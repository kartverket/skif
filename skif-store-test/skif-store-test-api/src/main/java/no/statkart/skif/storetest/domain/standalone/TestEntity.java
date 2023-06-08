package no.statkart.skif.storetest.domain.standalone;

import java.util.Objects;

/**
 * Brukes kun for lavnivå session factory testing. Det finnes ett slik objekt i database og ingen tester lager
 * nye.
 * @author Henrik Fredholm
 */
public class TestEntity {
    private Long id;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestEntity)) return false;

        TestEntity that = (TestEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(text, that.text)
                ;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(text);
        return result;
    }
}