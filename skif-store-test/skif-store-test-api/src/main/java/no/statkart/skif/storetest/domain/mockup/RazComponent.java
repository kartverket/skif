package no.statkart.skif.storetest.domain.mockup;

import java.io.Serializable;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class RazComponent implements Serializable{

    FooId<?> fooId;
    private String compText;

    public RazComponent() {
    }

    public FooId<?> getFooId() {
        return fooId;
    }

    public void setFooId(FooId<?> fooId) {
        this.fooId = fooId;
    }

    public String getCompText() {
        return compText;
    }

    public void setCompText(String compText) {
        this.compText = compText;
    }
}
