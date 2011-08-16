package no.statkart.matrikkel.build.utils.parser.sql.model.factory;

import no.statkart.matrikkel.build.utils.parser.sql.model.ExpressionVisitor;

/**
 * Abstract factory funksjoner for oppsett av visitorer.
 * <p/>
 * Det er tatt høyde for at implementasjonen kan benytte seg av
 * <a href="http://en.wikipedia.org/wiki/Chain-of-responsibility_pattern">chain of responsibility pattern</a> slik at oppgaven blir delegert til riktig instans av visitor.
 * <p/>
 * Modellen er løsriven ifra visitor implementasjon. Dette tilrettelegger da for minimale endringer i koden ved evt utøkelse/endring av funksjonalitet.
 * Dette blir tilbudt igjennom <a href="http://en.wikipedia.org/wiki/Abstract_factory_pattern">abstract factory patternet</a>.
 * <p/>
 * Se forøvrigt <a href="http://drjava.sourceforge.net/papers/DP4RDP-final.pdf">"Design patterns for Parsing"</a> av "Dung 'Zung' Nguyen" m.fl.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public interface VisitorFactoryInterface {

    public ExpressionVisitor makeVisitor();

    public ExpressionVisitor makeChainedVisitor(ExpressionVisitor successor);
}
