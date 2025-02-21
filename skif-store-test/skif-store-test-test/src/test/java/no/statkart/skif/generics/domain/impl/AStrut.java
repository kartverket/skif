package no.statkart.skif.generics.domain.impl;


/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public class AStrut<I extends AStrutId<?>> extends BaseStrut<I> {

    String funtion;


    public String getFuntion() {
        return funtion;
    }

    public void setFuntion(String funtion) {
        this.funtion = funtion;
    }

    // implementing methods
    @Override
    public I getId() {
        return id;
    }

    @Override
    public I setId(I strutId) {
        return id = strutId;
    }

}
