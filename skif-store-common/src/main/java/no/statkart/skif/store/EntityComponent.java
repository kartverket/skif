package no.statkart.skif.store;

/**
 * Interface som angir at et domeneobjekt er en komponent som har en id som er eksplisitt definert i domeneobjektet.
 * En {@code EntityComponent} kan inneholde {@code ValueObject} felter og collections av disse,
 * {@code CompositeComponent} felter (men ikke collections av disse), samt {@code EntityComponent} felter og
 * collections av disse.
 *
 * <P>{@code EntityComponent}-er som skal brukes i {@code Set} må definere {@link #equals(Object)} og
 * {@link #hashCode()}. Disse metoder må implementeres slik at {@code equals} returnere true for objekter som
 * skal regnes som equivalente. Det normale er å bruke feltene som utgør objektets logiske ident. Dersom
 * objektet skal understøtte endring av feltene som inngår i {@code equals} og {@code hashCode} må objektet
 * implementere spesialhåndtering av dette i henhold til SKIF-pattern for dette.
 *
 * <P>SKIF-rammeverket støtter ikke deling av {@code EntityComponent}-er. En {@code EntityComponent}-instans kan derfor
 * kun være innehold i en boble en gang. Dvs en {@code EntityComponent}-instans kan kun være medlem av en collection
 * eller bli pekt på av et felt. SKIF-rammeverket forsøker etter beste evne å håndheve dette og har best støtte når
 * {@code EntityComponentWithOwnerReference} implementeres.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public interface EntityComponent extends Component {

    /**
     * Hibernate 3.2 håndterer ikke covariant return type for id'er. Må derfor være definert som Long.
     * Velger alltid første metode som hedder getId() og som da vil returnerer Object
     */
    Long getId();

}
