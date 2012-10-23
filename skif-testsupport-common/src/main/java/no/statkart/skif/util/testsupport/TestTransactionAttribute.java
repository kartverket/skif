package no.statkart.skif.util.testsupport;

/**
 * Annotation for å angi transaction-attribute for server-test-metode i unittester av type ServerTestCase
 *
 * @author Henrik Fredholm
 * @see TestTransactionAttributeType
 * @since 2.1
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface TestTransactionAttribute {
    TestTransactionAttributeType value() default TestTransactionAttributeType.TX_NOT_SUPPORTED;
}
