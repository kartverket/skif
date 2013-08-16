package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt et er value object, dvs ikke har id, ikke har referanse til eiende
 * objekt og kun har immutable felter. Et ValueObject kan deles mellom bobler og komponenter uten at det
 * må kopiers da det aldrig endres og ikke inneholder owner. Ved lagring til database brytes delingen slik at
 * objektet lagres for hver sted det er brukt.
 *
 * <P>ValueObject-er lagres vanligvis i databasen i samme tabell som det eiende objektet. ValueObject-er
 * kan også brukes som komponenter i et sett eller liste. ValueObject-er skal ikke inneholde collections eller
 * komponenter som ikke selv er av type ValueObject.
 *
 * <P>Hvis ValueObject skal brukes i {@code Set} må objektet implementere {@link #equals(Object)} og {@link #hashCode()}.
 * Normalt brukes alle felter i objektet. Dersom settet som implementeres har uniqueness constraints kan dette implementeres
 * i databasen ved å definere passende primay key for tabellen som inneholder ValueObject-et. I tillegg til feltene
 * som inngår i ValueObject-et bør tabellen også ha et ownerId felt som peker på objektet som verdien gjelder for.
 *
 * <P>ValueObject er et marker-interface som strengt tatt ikke er påkrevd av rammeverket, dvs et objekt kan godt
 * brukes som et ValueObject uten at det implementere ValueObject interface-et. Typiske eksempler for dette er
 * standard Java klasser som String og Date.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public interface ValueObject extends Serializable {
}
