package no.statkart.skif.store.endringslogg;

import no.statkart.skif.store.BubbleObject;

/**
 * Klasse som mapper mellom endringsklasser og bobleklasser. Alle bobler som skal ha endringslogg må som minimum ha en
 * endringsklasse som representerer boblens basetype. Hvis endringsloggen skal kunne skelne mellom forskjellige subtyper av
 * boblen så må det lages endringsklasser for hver subtypen det skal skelnes mellom. og med tilsvarende arvehieraki mellom endringsklasser
 * som det finnes mellom bobleklasser. Det er ikke noe krav at hele hierakiet skal modelleres.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface Endring2DomainClassMapper {
    Class<? extends BubbleObject> getDomainClass(Class<? extends AbstractEndring<?,?>> endringClass);
    Class<? extends AbstractEndring> getEndringClass(Class<? extends BubbleObject> endringClass);
}
