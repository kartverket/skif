package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Date;

/**
 * @author Knut Inge Bøe
 */
public class RegistreringAnkeOverNekting extends RegistreringAnke{
    private Date nektetDato;

    @Override
    public RegistreringAnkeOverNektingId<?> getId() {
        return (RegistreringAnkeOverNektingId<?>) super.getId();
    }

}
