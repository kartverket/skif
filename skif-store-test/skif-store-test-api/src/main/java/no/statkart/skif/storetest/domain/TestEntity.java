package no.statkart.skif.storetest.domain;

/**
 *
 * @author Henrik Fredholm
 */
public class TestEntity {
    private Long id;
    private String text;

    public TestEntity() {
    }

    public TestEntity(Long id, String text) {
        this.id = id;
        this.text = text;
    }

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
}