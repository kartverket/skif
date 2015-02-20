package no.statkart.skif;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public enum ServiceMode {
    /**
     * Full server-client-modus.
     */
    JEE,

    /**
     * Single-VM-modus med Serializable-marshalling.
     */
    SINGLE_VM,
    /**
     * Single-VM-modus med mapping til og fra JAXB-klasser.
     */
    SINGLE_VM_XML,
}
