package no.statkart.skif.store.endringslogg;

import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.sequence.DefaultSequenceBlockAllocatorServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Håndterer grunnleggende generering av endringer. Prosjekter må i det minste lage en tynn implementasjon.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class AbstractEndringManager<E extends AbstractEndring> implements StoreSessionFinishListener {
    private final static Logger logger = LoggerFactory.getLogger(AbstractEndringManager.class);

    private final EndringManagerConfiguration<E> endringManagerConfiguration;

    private final Provider<Connection> connectionProvider;

    private final SequenceBlockAllocatorService sequenceBlockAllocatorService;
    private final String sequenceName;

    protected AbstractEndringManager(EndringManagerConfiguration<E> endringManagerConfiguration, Provider<Connection> connectionProvider, Configuration configuration) {
        this.endringManagerConfiguration = endringManagerConfiguration;
        this.connectionProvider = connectionProvider;

        this.sequenceBlockAllocatorService = new DefaultSequenceBlockAllocatorServiceImpl(connectionProvider, configuration) {
            @Override
            protected void commit(Connection con) throws SQLException {
                // Committing tas av container for endringsnummer
            }
        };
        this.sequenceName = configuration.getString(SkifConfigConstants.ENDRINGSNUMMER_SEQUENCE_NAME);
    }


    /**
     * Bestemmer hvilket tidspunkt som skal brukes for endringene. Standardoppførsel er å hente transaksjonstidspunkt
     * fra databasen, noe som forutsetter at den har historikk.
     *
     * @return tidspunktet for endringene
     * @since 2.3.0
     */
    protected Timestamp getEndringstidspunkt() {
        return SnapshotVersionSessionHelper.getTransactionTime(connectionProvider.get());
    }

    @Override
    public void onFinish(StoreServer storeServer) {
        LinkedHashSet<BubbleId<?>> insertedIds = storeServer.getInsertedIds();
        LinkedHashSet<BubbleId<?>> updatedIds = storeServer.getUpdatedIds();
        LinkedHashSet<BubbleId<?>> deletedIds = storeServer.getDeletedIds();

        if (insertedIds.size() > 0 || updatedIds.size() > 0 || deletedIds.size() > 0) {
            Timestamp tidspunkt = getEndringstidspunkt();

            List<AbstractEndring> endringer = new ArrayList<AbstractEndring>();

            for (BubbleId<?> bubbleId : insertedIds) {
                E endring = createEndring(bubbleId, Endringstype.Nyoppretting, tidspunkt);
                if (endring != null) {
                    decorateEndring(storeServer, endring);
                    endringer.add(endring);
                }
            }
            for (BubbleId<?> bubbleId : updatedIds) {
                E endring = createEndring(bubbleId, Endringstype.Oppdatering, tidspunkt);
                if (endring != null) {
                    decorateEndring(storeServer, endring);
                    endringer.add(endring);
                }
            }
            for (BubbleId<?> bubbleId : deletedIds) {
                E endring = createEndring(bubbleId, Endringstype.Sletting, tidspunkt);
                if (endring != null) {
                    decorateEndring(storeServer, endring);
                    endringer.add(endring);
                }
            }

            final int antall = endringer.size();
            if (antall > 0) {
                long nr = sequenceBlockAllocatorService.allocateSequenceBlock(sequenceName, antall) - antall + 1;
                for (AbstractEndring endring : endringer) {
                    Class<? extends BubbleId<? extends AbstractEndring>> idClass = BubbleIds.getBubbleIdClass(endring.getClass());
                    BubbleId<? extends AbstractEndring> id = BubbleIds.createInstance(idClass, nr++, SnapshotVersion.CURRENT);
                    endring.setId(id);
                    storeServer.insert(endring);
                }
            }
        }
    }

    private E createEndring(BubbleId<? extends BubbleObject> bubbleId, Endringstype endringstype, Timestamp tidspunkt) {
        Class<? extends E> endringClass = findEndringClass(bubbleId);
        if (endringClass != null) {
            final E endring;

            try {
                endring = endringClass.newInstance();
            } catch (InstantiationException e) {
                throw new ImplementationException(e);
            } catch (IllegalAccessException e) {
                throw new ImplementationException(e);
            }

            endring.setEndringstype(endringstype);
            endring.setEndringstidspunkt(tidspunkt);
            endring.setEndretBubbleId(bubbleId);

            return endring;
        } else {
            return null;
        }
    }

    protected Class<? extends E> findEndringClass(BubbleId<? extends BubbleObject> bubbleId) {
        return endringManagerConfiguration.findEndringClass(bubbleId.getType());
    }

    /**
     * Overstyr denne for å initialisere andre felter enn de som følger med {@link AbstractEndring}. Endringen har på
     * dette tidspunktet ingen id, og er følgelig ikke lagt inn i store.
     *
     * @param storeServer    store
     * @param endring        endringen som nettopp har blitt laget
     */
    protected void decorateEndring(StoreServer storeServer, E endring) {
    }

}
