package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.annotation.Cardinality;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface X2AAWithEntityComponentFinderService {
    enum Role implements RelationName {
        someBB,
        someCCs,
        role1BBs
    }

    @Relation(type = RelationType.INVERSE, cardinality = Cardinality.MANY, name = "someBB")
    Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvSomeBBIds(Collection<? extends X2BBOneId<?>> x2BBOneIds);

    @Relation(type = RelationType.INVERSE, cardinality = Cardinality.ONE, name = "someCCs")
    Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> findInvSomeCCsId(Collection<? extends X2CCManyId<?>> x2CCManyIds);

    @Relation(type = RelationType.INVERSE, cardinality = Cardinality.MANY, name = "role1BBs")
    Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvRole1BBIds(Collection<? extends X2BBOneId<?>> x2BBOneIds);

}
