package no.statkart.skif.store;


/**
 * Interface som må implementeres av domeneobekter som enten selv eller via subkomponenter har  bubbleId referanser som
 * inngår i en invers releasjon. Interfacet brukes får å kunne hente ut disse verdier når objektet knyttes til sin
 * owner og når boblen knyttes til eller fjernes fra  Store via insert, update or delete.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface InverseRelationParticipation {

    /**
     * Henter ut relasjonsnavne med tilhørende feltverdi
     *
     * @param collector collector
     */
    void collectInverseRelationValues(InverseRelationCollector collector);

}
