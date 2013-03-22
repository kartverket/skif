package no.statkart.skif.store.endringslogg;

import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    // TODO: Test
    private long id = 0;

    protected AbstractEndringManager(Provider<ServiceRequestContext> contextProvider, Collection<Class<? extends AbstractEndring>> endringsklasser) {
        this.contextProvider = contextProvider;
        endringklasseMap = new HashMap<Class<? extends BubbleId>, Class<? extends AbstractEndring>>(endringsklasser.size());

        for (Class<? extends AbstractEndring> endringClass : endringsklasser) {
            endringklasseMap.put(findIdClassForEndringClass(endringClass), endringClass);
        }
    }

    private static Class<? extends BubbleId> findIdClassForEndringClass(Class<? extends AbstractEndring> endringClass) {
        TypeVariable<? extends Class<? extends AbstractEndring>>[] typeParameters = endringClass.getTypeParameters();
        if (typeParameters.length != 1) {
            throw new ImplementationException("Endringsklasse " + endringClass + " har ikke én og bare én typeparameter", logger);
        }

        Type[] bounds = typeParameters[0].getBounds();
        if (bounds.length != 1) {
            throw new ImplementationException("Endringsklasse " + endringClass + " typeparameter som ikke har én og bare én bounds", logger);
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

    @Override
    public void onFinish(StoreServer storeServer) {
        ServiceRequestContext serviceRequestContext = contextProvider.get();
        String principal = serviceRequestContext.getCallerPrincipal().getName();
        Date tidspunkt = new Date();

        for (BubbleId<?> bubbleId : storeServer.getInsertedIds()) {
            createEndring(storeServer, tidspunkt, principal, bubbleId, 1);
        }
        for (BubbleId<?> bubbleId : storeServer.getUpdatedIds()) {
            createEndring(storeServer, tidspunkt, principal, bubbleId, 2);
        }
        for (BubbleId<?> bubbleId : storeServer.getUpdatedIds()) {
            createEndring(storeServer, tidspunkt, principal, bubbleId, 3);
        }
    }

    private void createEndring(StoreServer storeServer, Date tidspunkt, String brukernavn, BubbleId<?> bubbleId, int endringstype) {
        Class<? extends AbstractEndring> endringClass = endringklasseMap.get(bubbleId.getClass());
        if (endringClass != null) {
            AbstractEndring<?> endring = createEndring(endringClass);

            endring.setEndringstype(endringstype);
            endring.setEndringstidspunkt(tidspunkt);
            endring.setBrukernavn(brukernavn);
            endring.setEndretBubbleId(bubbleId);

            storeServer.insert(endring);
        }
    }

    private AbstractEndring<?> createEndring(Class<? extends AbstractEndring> endringClass) {
        try {
            AbstractEndring endring = endringClass.newInstance();
            Class<? extends BubbleId<? extends AbstractEndring>> endringIdClass = BubbleIds.getBubbleIdClass(endring.getClass());
            BubbleId<? extends AbstractEndring> endringId = BubbleIds.createInstance(endringIdClass, ++id, SnapshotVersion.CURRENT);
            endring.setId(endringId);
            return endring;
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }
}
