package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt et er value object, dvs ikke har en id, ikke har referanse til eiende
 * objekt og kun har felter som ikke kan endres. ValueObject kan deles mellom bobler og andre objekter. ValueObject
 * lagres vanligvis i databasen i samme tabell som det eiende objektet. De kan eventuelt også brukes som komponenter
 * i et sett eller liste. ValueComponent skal ikke inneholde collections eller komponenter som ikke selv er
 * ValueObject.
 *
 * <P>ValueObject må implementere {@link #equals(Object)} slik at metoden returnerer {@code true} for objekter som skal
 * betraktes som ekvivalente, dvs har samme logiske id. Equals metoden brukes bl.a av Hibernate for
 * vedlikehold av {@code Set<ValueObject>}. Normalt vil equals metoden omfatte alle objektets felter, men i noen tilfeller
 * kan det være nødvendig å utelade felter for å få ønsket ekvivalens (for eksempel hvis et Beløp kan ha verdi, valuta og
 * et kommentarfelt og {@code Set<Beløp>} ikke skal kunne inneholde flere Belop som har samme verdi og valuta.)
 *
 * <P>ValueObject er et marker interface som ikke er påkrevd, dvs et objekt kan godt brukes som et ValueObject uten at
 * det implementere ValueObject interfacet. Typiske eksempler for dette er standard Java klasser som String og Date.
 *
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public interface ValueObject extends Serializable {
}
