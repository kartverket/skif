package no.statkart.skif.service.proxy;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.annotation.ServiceContextMapped;
import no.statkart.skif.service.annotation.SuppressSnapshotVersionMapping;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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

    public SnapshotVersionD2WResult analyseD2W(Method apiMethod, Object[] args) {
        Type[] types = apiMethod.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = apiMethod.getParameterAnnotations();
        int length = types.length;

        // Regel 1: ingen parametre eller @SuppressSnapshotVersionMapping på metodenivå  => Bruk SnapshotVersionContext
        if (length == 0 || hasSuppressSVMappingAnnotation(apiMethod)) return new SnapshotVersionD2WResult(length);

        // Regel 2: siste parameter er av type SnapshotVersion og er  annotert med @ServiceContextMapped. Denne skal da mappes via ServiceContext og ikke som egen parameter
        if (isSnapshotVersionType(types[length - 1]) && hasServiceContextMappedAnnotation(parameterAnnotations[length - 1]))
            return new SnapshotVersionD2WResult((SnapshotVersion)args[length - 1], length - 1);

        // Regel 3: Første parameter fra start som ikke er annotert med {@code @Nullable} eller {@code SuppressSnapshotVersionMapping} og som
        // er en subtype av SnapshotVersion, BubbleId eller Collection<? extends BubbleId>.
        for (int i=0; i<length; i++) {
            if (!hasIngnoreAnnotation(parameterAnnotations[i])) {
                if (isSnapshotVersionType(types[i])) {
                    return new SnapshotVersionD2WResult((SnapshotVersion)args[i], length);
                }
                if (isBubbleIdType(types[i])) {
                    BubbleId id = (BubbleId) args[i];
                    if (id==null) {
                        throw new ImplementationException(String.format("Parameter with index '%d' of subtype BubbleId is null, did you forget to annotate with @Nullable?", i));
                    }
                    return new SnapshotVersionD2WResult(id.getSnapshotVersion(), length);
                }
                if (isCollectionOfBubbleIdType(types[i])) {
                    Collection<? extends BubbleId<?>> ids =(Collection<? extends BubbleId<?>>) args[i];
                    if (ids.isEmpty()) {
                        return new SnapshotVersionD2WResult(length);
                    } else {
                        BubbleId<?> id = ids.iterator().next();
                        if (id==null) {
                            throw new ImplementationException(String.format("Parameter with index '%d' of subtype BubbleId is null, did you forget to annotate with @Nullable?", i));
                        }
                        return new SnapshotVersionD2WResult(id.getSnapshotVersion(), length);
                    }
                }
            }
        }
        // Regel 4: Ingen opplagt parameter => bruk SnapshotVersionContext
        return new SnapshotVersionD2WResult(length);
    }


    public SnapshotVersionW2DResult analyseW2D(Method apiMethod) {
        Type[] types = apiMethod.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = apiMethod.getParameterAnnotations();
        int length = types.length;

        // Regel 1: ingen parametre eller @SuppressSnapshotVersionMapping på metodenivå => apimethod skal ikke ha egen snapshotVersion parameter
        if (length == 0 || hasSuppressSVMappingAnnotation(apiMethod) ) return new SnapshotVersionW2DResult(false, length);

        // Regel 2: siste parameter er av type SnapshotVersion og er annotert med @ServiceContextMapped => apimethod har en ekstra SnapshotVersion parameter
        if (isSnapshotVersionType(types[length - 1]) && hasServiceContextMappedAnnotation(parameterAnnotations[length - 1]))
            return new SnapshotVersionW2DResult(true, length-1); // length må være en mindre fordi siste argument skal ikke komme fra wsapi args men må settes manuelt av proxy

        return new SnapshotVersionW2DResult(false, length);
    }

    /**
     * Returnerer true hvis typen har annotasjon @ServiceContextMapped
     */
    private boolean hasServiceContextMappedAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType()==ServiceContextMapped.class) return true;
        }
        return false;
    }

    /**
     * Returnerer true hvis typen har annotasjon @Nullable eller @SuppressSnapshotVersionMapping
     */
    private boolean hasIngnoreAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType()==Nullable.class) return true;
            if (annotation.annotationType()==SuppressSnapshotVersionMapping.class) return true;
        }
        return false;
    }

    private boolean hasSuppressSVMappingAnnotation(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType()==SuppressSnapshotVersionMapping.class) return true;
        }
        return false;
    }

    private boolean isSnapshotVersionType(Type type) {
        return type==SnapshotVersion.class;
    }

    private boolean isBubbleIdType(Type type) {
        return BubbleId.class.isAssignableFrom(TypeToken.of(type).getRawType());
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
