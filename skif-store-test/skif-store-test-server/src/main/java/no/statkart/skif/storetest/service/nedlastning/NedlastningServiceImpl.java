package no.statkart.skif.storetest.service.nedlastning;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.*;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.endringslogg.EndringManagerConfiguration;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class NedlastningServiceImpl extends no.statkart.skif.store.service.NedlastningServiceImpl implements NedlastningService {

    @Inject
    public NedlastningServiceImpl(Provider<SnapshotVersion> snapshotVersionProvider, Provider<SessionSelector> sessionSelectorProvider, EndringManagerConfiguration endringManagerConfiguration, Store store) {
        super(snapshotVersionProvider, sessionSelectorProvider, endringManagerConfiguration, store);
    }
}
