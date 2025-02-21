package no.statkart.skif.store;

/**
 * Hjelpeklasse for å pakke alle felter i et objekt i en streng som kan skrives til databasen. Nesting støttes ikke.
 * Objekter som implementerer dette interface bør også implementere en public constructor som tar
 * {@link ConcatenatedFields} som eneste argument.
 */
public interface ConcatenatedFieldsSerialization {
    ConcatenatedFields toConcatinatedFields();
}
