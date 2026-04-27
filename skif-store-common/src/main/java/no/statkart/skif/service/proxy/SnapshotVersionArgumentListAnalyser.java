package no.statkart.skif.service.proxy;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.annotation.ServiceContextMapped;
import no.statkart.skif.service.annotation.SuppressSnapshotVersionMapping;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Klasse som henter ut {@code SnapshotVersion} fra parameterliste for gitt metode
 *
 * @author Henrik Frehholm
 * @since 2.4
 */
public class SnapshotVersionArgumentListAnalyser {
    /**
     * Legacy klasse fra JSR305 (finnes ikke i Java/Jakarta EE). Ligger bare her i tilfelle konsumenter fortsatt bruker JSR305
     */
    static final Class<? extends Annotation> JAVAX_NULLABLE_CLAZZ = tryFindOptionalClass("javax.annotation.Nullable");
    /**
     * Annotasjon som ble innført først til Jakarta EE10 (finnes ikke i <=EE9)
     */
    static final Class<? extends Annotation> JAKARTA_NULLABLE_CLAZZ = tryFindOptionalClass("jakarta.annotation.Nullable");
    /**
     * Finnes i org.jspecify:jspecify:1.0.0 
     * JSpecify følger med Guava 33.4 og nyere 
     */
    static final Class<? extends Annotation> JSPECIFY_NULLABLE_CLAZZ = tryFindOptionalClass("org.jspecify.annotations.Nullable");

    private static Class<? extends Annotation> tryFindOptionalClass(String className) {
        try {
            return Class.forName(className).asSubclass(Annotation.class);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public SnapshotVersionD2WResult analyseD2W(Method apiMethod, Object[] args) {
        final Parameter[] parameters = apiMethod.getParameters();
        final int length = parameters.length;

        // Regel 1: ingen parametre eller @SuppressSnapshotVersionMapping på metodenivå  => Bruk SnapshotVersionContext
        if (length == 0 || apiMethod.isAnnotationPresent(SuppressSnapshotVersionMapping.class)) 
            return new SnapshotVersionD2WResult(0);

        // Regel 2: siste parameter er av type SnapshotVersion og er  annotert med @ServiceContextMapped. Denne skal da mappes via ServiceContext og ikke som egen parameter
        final Parameter lastParameter = parameters[length - 1];
        if (lastParameter.getType() == SnapshotVersion.class && lastParameter.isAnnotationPresent(ServiceContextMapped.class))
            return new SnapshotVersionD2WResult((SnapshotVersion) args[length - 1], length - 1);

        // Regel 3: Første parameter fra start som ikke er annotert med {@code @Nullable} eller {@code SuppressSnapshotVersionMapping} og som
        // er en subtype av SnapshotVersion, BubbleId eller Collection<? extends BubbleId>.
        for (int i = 0; i < length; i++) {
            final Parameter parameter = parameters[i];
            if (hasIgnoreAnnotation(parameter)) continue;

            if (parameter.getType() == SnapshotVersion.class) {
                return new SnapshotVersionD2WResult((SnapshotVersion) args[i], length);
            }

            if (BubbleId.class.isAssignableFrom(parameter.getType())) {
                BubbleId<?> id = (BubbleId<?>) args[i];
                if (id == null) {
                    throw new ImplementationException(String.format("Parameter with index '%d' of subtype BubbleId is null, did you forget to annotate with @Nullable?", i));
                }
                return new SnapshotVersionD2WResult(id.getSnapshotVersion(), length);
            }
            
            if (isCollectionOfBubbleIdType(parameter.getParameterizedType())) {
                @SuppressWarnings("unchecked") 
                Collection<? extends BubbleId<?>> ids = (Collection<? extends BubbleId<?>>) args[i];
                if (ids.isEmpty()) {
                    return new SnapshotVersionD2WResult(length);
                } else {
                    BubbleId<?> id = ids.iterator().next();
                    if (id == null) {
                        throw new ImplementationException(String.format("Parameter with index '%d' of subtype BubbleId is null, did you forget to annotate with @Nullable?", i));
                    }
                    return new SnapshotVersionD2WResult(id.getSnapshotVersion(), length);
                }
            }
        }
        
        // Regel 4: Ingen opplagt parameter => bruk SnapshotVersionContext
        return new SnapshotVersionD2WResult(length);
    }


    public SnapshotVersionW2DResult analyseW2D(Method apiMethod) {
        final Parameter[] parameters = apiMethod.getParameters();
        int length = parameters.length;

        // Regel 1: ingen parametre eller @SuppressSnapshotVersionMapping på metodenivå => apimethod skal ikke ha egen snapshotVersion parameter
        if (length == 0 || apiMethod.isAnnotationPresent(SuppressSnapshotVersionMapping.class))
            return new SnapshotVersionW2DResult(false, 0);

        // Regel 2: siste parameter er av type SnapshotVersion og er annotert med @ServiceContextMapped => apimethod har en ekstra SnapshotVersion parameter
        final Parameter lastParameter = parameters[length - 1];
        if (lastParameter.getType() == SnapshotVersion.class && lastParameter.isAnnotationPresent(ServiceContextMapped.class))
            return new SnapshotVersionW2DResult(true, length - 1); // length må være en mindre fordi siste argument skal ikke komme fra wsapi args men må settes manuelt av proxy

        return new SnapshotVersionW2DResult(false, length);
    }

    /**
     * Returnerer true hvis typen har annotasjon @Nullable eller @SuppressSnapshotVersionMapping
     */
    private boolean hasIgnoreAnnotation(Parameter parameter) {
        return JSPECIFY_NULLABLE_CLAZZ != null && parameter.getAnnotatedType().isAnnotationPresent(JSPECIFY_NULLABLE_CLAZZ)
            || JAVAX_NULLABLE_CLAZZ != null && parameter.isAnnotationPresent(JAVAX_NULLABLE_CLAZZ)
            || JAKARTA_NULLABLE_CLAZZ != null && parameter.isAnnotationPresent(JAKARTA_NULLABLE_CLAZZ)
            || parameter.isAnnotationPresent(SuppressSnapshotVersionMapping.class)
            ;
    }

    private boolean isCollectionOfBubbleIdType(Type parameterType) {
        TypeToken<?> parameterTypeToken = TypeToken.of(parameterType);
        if (Collection.class.isAssignableFrom(parameterTypeToken.getRawType()) && parameterTypeToken.getType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterTypeToken.getType();
            TypeToken<?> parameterizedTypeToken = TypeToken.of(parameterizedType.getActualTypeArguments()[0]);
            return BubbleId.class.isAssignableFrom(parameterizedTypeToken.getRawType());
        } else {
            return false;
        }
    }


}
