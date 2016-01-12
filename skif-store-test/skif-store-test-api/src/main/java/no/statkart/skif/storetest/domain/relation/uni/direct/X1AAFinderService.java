package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.relation.cache.annotation.Cardinality;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface X1AAFinderService {
    public enum Role implements RelationName {
        someBB,
        someCCs,
        uniqueOnX1AA,
        nonUniqueOnX1AA,
        x1AAForIdent,
        x1BBOneForIdent
    }

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.MANY, name="someBB")
    Map<X1BBOneId<?>, Set<X1AAId<?>>> findInvSomeBBIds(Collection<? extends X1BBOneId<?>> x1BBOneIds);

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.ONE, name="someCCs")
    Map<X1CCManyId<?>, X1AAId<?>> findInvSomeCCsId(Collection<? extends X1CCManyId<?>> x1CCManyIds);

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.ONE, name="uniqueOnX1AA")
    public Map<String, X1AAId<?>> findX1AAIdsForUniqueOnX1AA(Collection<String> textValues);

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.MANY, name="nonUniqueOnX1AA")
    public Map<String, Set<X1AAId<?>>> findX1AAIdsForNonUniqueOnX1AA(Collection<String> textValues);

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.MANY, name="x1AAForIdent")
    public Map<X1AAIdent, Set<X1AAId<?>>> findX1AAIdsForIdents(Collection<X1AAIdent> idents);

    @Relation(type= RelationType.INVERSE, cardinality= Cardinality.MANY, name="x1BBOneForIdent")
    public Map<X1BBOneIdent, Set<X1BBOneId<?>>> findX1BBOneIdsForIdents(Collection<X1BBOneIdent> idents);
}
