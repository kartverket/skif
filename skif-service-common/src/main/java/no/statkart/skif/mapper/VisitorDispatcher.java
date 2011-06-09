package no.statkart.skif.mapper;

/**
 * Dispatcher interface som brukes i visitor pattern.
 * @See Visitor
 * @See ArgumentTypeBasedVisitorDispatcher
 * @author Henrik Fredholm
 */
public interface VisitorDispatcher {
    /**
     * Delegerer kall videre til underliggende <code>Visitor</code> objekt. Hvilken <code>visit<code> metode
     * som blir kallt på visitor objektet avhenger av faktisk objekttype for <code>o</code>. Dersom <code>o</code>
     * er en <code>Collection</code> eller array vil den underliggende visitor bli kallt på hvert enkelt element
     * @param o
     */
    public void visit(Object o);
}