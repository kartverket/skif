package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Bubbles;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.HashSet;
import java.util.Set;

/**
 * Boble som bruker {@code EmptyCollectionsOptimizer} til å laste tomme collections uten å gjøre spørringer mot databasen.
 * Boblen har 2 collections, children1 og  children2 som bruker EmptyCollectionsOptimizer og en collection, children3,
 * som ikke bruker EmptyCollectionsOptimizer.
 * <p>
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 */
public class ParentBubbleEmptyColOptimizer extends AbstractBubbleObject implements StoreTestBubble {
    /**
     * Intern flag som Hibernate bruker og vedlikeholder ifm effektiv lasting av tomme collections i boblen.
     * Feltet er transient fordi verdien kun brukes internt på serveren når objektet er knyttet til Hibernate sessionen.
     * Feltet ikke skal kunne settes for kopierte objekter som kommer fra klient eller kommer fra unit of work på server
     */
    @SuppressWarnings("unused") //brukes av hibernate
    private transient long emptyCollectionsFlag;

    private String text;
    private Set<ChildBubbleEmptyColOptimizerId> children1Ids = new HashSet<>(); // Bruker bitt 0, se mapping fil
    private Set<ChildBubbleEmptyColOptimizerId> children2Ids = new HashSet<>(); // Bruker bitt 1, se mapping fil
    private Set<ChildBubbleEmptyColOptimizerId> children3Ids = new HashSet<>(); // Bruker ikke flagget.

    public ParentBubbleEmptyColOptimizer() {
    }

    public ParentBubbleEmptyColOptimizer(BubbleId<?> id) {
        super(id);
    }

    public ParentBubbleEmptyColOptimizer(BubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    public long getEmptyCollectionsFlag() {
        return emptyCollectionsFlag;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId(id);
    }

    @Override
    public ParentBubbleEmptyColOptimizerId<?> getId() {
        return (ParentBubbleEmptyColOptimizerId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<ChildBubbleEmptyColOptimizerId> getChildren1Ids() {
        return children1Ids;
    }

    public ParentBubbleEmptyColOptimizer setChildren1Ids(Set<ChildBubbleEmptyColOptimizerId> children1Ids) {
        Bubbles.setFrom(this.children1Ids, children1Ids);
        return this;
    }

    public Set<ChildBubbleEmptyColOptimizerId> getChildren2Ids() {
        return children2Ids;
    }

    public ParentBubbleEmptyColOptimizer setChildren2Ids(Set<ChildBubbleEmptyColOptimizerId> children2Ids) {
        Bubbles.setFrom(this.children2Ids, children2Ids);
        return this;
    }

    public Set<ChildBubbleEmptyColOptimizerId> getChildren3Ids() {
        return children3Ids;
    }

    public ParentBubbleEmptyColOptimizer setChildren3Ids(Set<ChildBubbleEmptyColOptimizerId> children3Ids) {
        Bubbles.setFrom(this.children3Ids, children3Ids);
        return this;
    }

}
