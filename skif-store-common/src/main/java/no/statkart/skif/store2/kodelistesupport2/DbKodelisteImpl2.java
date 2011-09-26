package no.statkart.skif.store2.kodelistesupport2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodelisteImpl2 extends KodelisteImpl2 implements DbKodeliste2 {
    private String beskrivelsesKey;

    @Override
    public DbKodelisteIdImpl2 getId() {
        return (DbKodelisteIdImpl2) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }

}
