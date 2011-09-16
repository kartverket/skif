package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbBubbleKode;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeId;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeliste;
import org.hibernate.Session;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbBubbleKodeLoader {
    void loadKoder(Session session, DbBubbleKodeliste kodeliste, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap);
}
