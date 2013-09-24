package no.statkart.skif.store;

/**
 * Interface for lokalisering av object
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface Localizable {
    void localize(String localeString);

    void updateLocalized(String localeString);

    // TODO: Midlertidige hacks i forbindelse med omskriving av koderammeverk
    BubbleId<?> getId();
    void setNavn(String navn);
    void setBeskrivelse(String navn);
}
