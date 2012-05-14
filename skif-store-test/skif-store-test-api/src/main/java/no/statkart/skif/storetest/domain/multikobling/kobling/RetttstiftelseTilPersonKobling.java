package no.statkart.skif.storetest.domain.multikobling.kobling;

import no.statkart.skif.store.multikobling.Kobling;
import no.statkart.skif.store.multikobling.KoblingFactory;
import no.statkart.skif.storetest.domain.multikobling.PersonId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RetttstiftelseTilPersonKobling extends Kobling<RettsstiftelsePersonRolle, PersonId<?>> {
    public PersonId personId;

    public static KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>> KOBLING_FACTORY =
            new RettsstiftelsePersonRollePersonIdKoblingFactory();

    public RetttstiftelseTilPersonKobling() {
    }

    public RetttstiftelseTilPersonKobling(RettsstiftelsePersonRolle rolle, PersonId<?> id) {
        this.rolle = rolle;
        this.personId = id;
    }

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelsePersonRolle.valueOf(rolle);
    }

    @Override
    protected PersonId<?> getValue() {
        return personId;
    }

    @Override
    protected void setValue(PersonId<?> value) {
        personId = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetttstiftelseTilPersonKobling that = (RetttstiftelseTilPersonKobling) o;

        if (rolle != null ? !rolle.equals(that.rolle) : that.rolle != null) return false;
        if (personId != null ? !personId.equals(that.personId) : that.personId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return personId != null ? personId.hashCode() : 0;
    }

    private static class RettsstiftelsePersonRollePersonIdKoblingFactory implements KoblingFactory<RettsstiftelsePersonRolle, PersonId<?>> {
        @Override
        public Kobling<RettsstiftelsePersonRolle, PersonId<?>> create(RettsstiftelsePersonRolle rolle, PersonId<?> targetId) {
            return new RetttstiftelseTilPersonKobling(rolle, targetId);
        }
    }
}
