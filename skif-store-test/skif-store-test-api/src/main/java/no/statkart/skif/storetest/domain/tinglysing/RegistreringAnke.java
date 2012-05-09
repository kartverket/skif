package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Date;

/**
 * @author Knut Inge Bøe
 */
public class RegistreringAnke extends PaategningPaaRettsstiftelser {
    private Date oversendtLagmannsretten;

    @Override
    public RegistreringAnkeId<?> getId() {
        return (RegistreringAnkeId<?>) super.getId();
    }

    public Date getOversendtLagmannsretten() {
        return oversendtLagmannsretten;
    }

    public void setOversendtLagmannsretten(Date oversendtLagmannsretten) {
        this.oversendtLagmannsretten = oversendtLagmannsretten;
    }
}
