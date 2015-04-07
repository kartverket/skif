package no.statkart.skif.store.relation.cache;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * Et globalt trådsikkert register over alle konfigurerte {@link RelationStrategy}-objekter. Siden java metode
 * objekter ikke er unike knyttes strategien til en key som beregnes ut fra metodenavn og navn på declaringClass.
 *
 * @author Henrik Fredholm
 * @since 2.6.0
*/
@Singleton
public class RelationCacheRegistry {
    final ConcurrentHashMap<String, RelationStrategy> registry = new ConcurrentHashMap<String, RelationStrategy>();


    public RelationStrategy getStrategy(Method method) {
        // Trenger en unik key for metoden. Kan ikke bruke metode objektet. Må ta med klasse da metodenavn i seg selv ikke er unikt.
        String key = method.getName() + ":" + method.getDeclaringClass().getName();
        RelationStrategy strategy = registry.get(key);
        if (strategy == null) {
            strategy = createRelationStrategy(method);
            RelationStrategy existing = registry.putIfAbsent(key, strategy);
            if (existing != null) {
                strategy = existing;
            }
        }
        return strategy;
    }

    private RelationStrategy createRelationStrategy(Method method) {
        Relation annotation = method.getAnnotation(Relation.class);
        if (annotation != null) {
            checkState(annotation.type() == RelationType.INVERSE);
            Class<? extends Enum> enumClass = SkifUtil.classForName(method.getDeclaringClass().getCanonicalName() + "$Role");
            return new InverseRelationStrategy((RelationName) Enum.valueOf(enumClass, annotation.name()));
        } else {
            return RelationStrategy.NO_CACHING;
        }
    }
}
