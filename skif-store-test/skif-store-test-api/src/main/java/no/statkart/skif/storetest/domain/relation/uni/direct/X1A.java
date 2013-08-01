package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * Klasse for å test unidireksjonelle relasjoner. Klasen har 3 forskjellige typer relasjoner
 * <ul>
 *     <li>Enkelt relasjon til X1Bone</li>
 *     <li>Mange relasjon til X1CMany</li>
 *     <li>En-til-en relasjon til X1DUnique</li>
 * </ul>
 *
 * De 3 relaterte klassene implementerer en finder for å navigerer relasjonen i motsatt rettning.
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1A extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    private X1BOneId bId;
//    private Set<X1CMany> x1CManySet;
//    private X1DUnique x1DUnique;
//    private Set<X1EManyMany> x1EManyManySet;

    @Override
    public X1AId<?> getId() {
        return (X1AId<?>) super.getId();
    }

    public X1BOneId getbId() {
        return bId;
    }

    public void setbId(X1BOneId bId) {
        this.bId = bId;
    }
}
