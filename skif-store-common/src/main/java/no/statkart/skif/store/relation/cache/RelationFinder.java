package no.statkart.skif.store.relation.cache;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.relation.cache.annotation.Relation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Henrik Fredholm
 */
public class RelationFinder {
    private final RelationName relationName;
    private final Object finderService;
    private final Method finderMethod;

    public RelationFinder(RelationName relationName, Store store) {
        Method foundMethod = null;
        this.relationName = relationName;
        Class<?> finderServiceClass = relationName.getClass().getEnclosingClass();
        checkArgument(finderServiceClass != null, "RelationName must be defined as an inner class of FinderService");
        for (Method m : finderServiceClass.getDeclaredMethods()) {
            Relation annotation = m.getAnnotation(Relation.class);
            if (annotation != null && annotation.name().equals(relationName.toString())) {
                foundMethod = m;
                break;
            }
        }
        if ( foundMethod==null) {
            throw new ImplementationException("Could not find method annotated with name" + relationName + " in FinderService: " + finderServiceClass.getCanonicalName() );
        }
        finderMethod = foundMethod;
        finderService = store.getInstance(finderServiceClass);
    }

    public RelationName getRelationName() {
        return relationName;
    }

    public <T> Map<BubbleId<?>, T> call(Set<? extends BubbleId<?>> ids) {
        try {
            return (Map<BubbleId<?>, T>) finderMethod.invoke(finderService, ids);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e.getTargetException());
        }
    }
}
