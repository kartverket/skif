package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.relation.cache.annotation.Cardinality;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface X1AAFinderService {
    public enum Role implements RelationName {
        someBB
    }

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.MANY, name="someBB")
    Map<X1BBOneId<?>, Set<X1AAId<?>>> findInvSomeBBIds(Collection<X1BBOneId<?>> x1BBOneIds);
}
