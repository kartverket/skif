package no.statkart.skif.store.endringslogg;

import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.sequence.DefaultSequenceBlockAllocatorServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
public abstract class AbstractEndringManager implements StoreSessionFinishListener {
    private final static Logger logger = LoggerFactory.getLogger(AbstractEndringManager.class);

    private final Map<Class<? extends BubbleId>, Class<? extends AbstractEndring>> endringklasseMap;
    private final Provider<ServiceRequestContext> contextProvider;
    private final Provider<Connection> connectionProvider;

    private final SequenceBlockAllocatorService sequenceBlockAllocatorService;
    private final String sequenceName;

    protected AbstractEndringManager(Collection<? extends Class<? extends AbstractEndring>> endringsklasser, Provider<ServiceRequestContext> contextProvider, Provider<Connection> connectionProvider, Configuration configuration) {
        this.contextProvider = contextProvider;
        this.connectionProvider = connectionProvider;

        this.sequenceBlockAllocatorService = new DefaultSequenceBlockAllocatorServiceImpl(connectionProvider, configuration) {
            @Override
            public void commit(Connection con) throws SQLException {
                // Committing tas av container for endringsnummer
            }
        };
        this.sequenceName = configuration.getString(SkifConfigConstants.ENDRINGSNUMMER_SEQUENCE_NAME);

        endringklasseMap = new HashMap<Class<? extends BubbleId>, Class<? extends AbstractEndring>>(endringsklasser.size());
        for (Class<? extends AbstractEndring> endringClass : endringsklasser) {
            endringklasseMap.put(findIdClassForEndringClass(endringClass), endringClass);
        }
    }

    public static Class<? extends BubbleId> findIdClassForEndringClass(Class<? extends AbstractEndring> endringClass) {
        TypeVariable<? extends Class<? extends AbstractEndring>>[] typeParameters = endringClass.getTypeParameters();
        if (typeParameters.length != 1) {
            throw new ImplementationException("Endringsklasse " + endringClass + " har ikke én og bare én typeparameter", logger);
        }

        Type[] bounds = typeParameters[0].getBounds();
        if (bounds.length != 1) {
            throw new ImplementationException("Endringsklasse " + endringClass + " typeparameter som ikke har én og bare én bound", logger);
        }

        final Class<? extends BubbleId> bubbleIdClass;
        if (bounds[0] instanceof Class) {
            Class<?> clazz = (Class) bounds[0];
            try {
                bubbleIdClass = clazz.asSubclass(BubbleId.class);
            } catch (ClassCastException e) {
                throw new ImplementationException("Endringsklasse " + endringClass + " har typeparameter " + clazz + " som ikke er BubbleId", logger);
            }
        } else if (bounds[0] instanceof ParameterizedType) {
            ParameterizedType bound = (ParameterizedType) bounds[0];
            Class<?> clazz = (Class) bound.getRawType();
            try {
                bubbleIdClass = clazz.asSubclass(BubbleId.class);
            } catch (ClassCastException e) {
                throw new ImplementationException("Endringsklasse " + endringClass + " har typeparameter " + clazz + " som ikke er BubbleId", logger);
            }
        } else {
            throw new ImplementationException("Endringsklasse " + endringClass + " har typeparameter av ikke-støttet type " + bounds[0].getClass(), logger);
        }

        return bubbleIdClass;
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
        ServiceRequestContext serviceRequestContext = contextProvider.get();
        String principal = serviceRequestContext.getCallerPrincipal().getName();
        Timestamp tidspunkt = getEndringstidspunkt();

        List<AbstractEndring> endringer = new ArrayList<AbstractEndring>();

        for (BubbleId<?> bubbleId : storeServer.getInsertedIds()) {
            AbstractEndring<?> endring = createEndring(storeServer, tidspunkt, principal, bubbleId, Endringstype.Nyoppretting);
            if (endring != null) {
                endringer.add(endring);
            }
        }
        for (BubbleId<?> bubbleId : storeServer.getUpdatedIds()) {
            AbstractEndring<?> endring = createEndring(storeServer, tidspunkt, principal, bubbleId, Endringstype.Oppdatering);
            if (endring != null) {
                endringer.add(endring);
            }
        }
        for (BubbleId<?> bubbleId : storeServer.getDeletedIds()) {
            AbstractEndring<?> endring = createEndring(storeServer, tidspunkt, principal, bubbleId, Endringstype.Sletting);
            if (endring != null) {
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

    private AbstractEndring<?> createEndring(StoreServer storeServer, Timestamp tidspunkt, String brukernavn, BubbleId<?> bubbleId, Endringstype endringstype) {
        Class<? extends AbstractEndring> endringClass = findEndringClassForIdClass(bubbleId);
        if (endringClass != null) {
            final AbstractEndring<?> endring;

            try {
                endring = endringClass.newInstance();
            } catch (InstantiationException e) {
                throw new ImplementationException(e);
            } catch (IllegalAccessException e) {
                throw new ImplementationException(e);
            }

            endring.setEndringstype(endringstype);
            endring.setEndringstidspunkt(tidspunkt);
            endring.setBrukernavn(brukernavn);
            endring.setEndretBubbleId(bubbleId);

            decorateEndring(storeServer, endring);

            return endring;
        } else {
            return null;
        }
    }

    protected Class<? extends AbstractEndring> findEndringClassForIdClass(BubbleId<?> bubbleId) {
        for (Class<?> idClass = bubbleId.getClass(); idClass != null; idClass = idClass.getSuperclass()) {
            //noinspection SuspiciousMethodCalls
            Class<? extends AbstractEndring> endringClass = endringklasseMap.get(idClass);
            if (endringClass != null) {
                return endringClass;
            }
        }
        return null;
    }

    /**
     * Overstyr denne for å initialisere andre felter enn de som følger med {@link AbstractEndring}. Endringen har på
     * dette tidspunktet ingen id, og er følgelig ikke lagt inn i store.
     *
     * @param storeServer    store
     * @param endring        endringen som nettopp har blitt laget
     */
    protected void decorateEndring(StoreServer storeServer, AbstractEndring<?> endring) {
    }

}
