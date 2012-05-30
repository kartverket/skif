package no.statkart.skif.storetest.domain.demo;

import com.google.inject.Inject;
import no.statkart.skif.store.Store;

import java.io.Serializable;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class RazComponent implements Serializable{

    FooId<?> fooId;
    private String compText;

    @Inject
    private Store store;

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
