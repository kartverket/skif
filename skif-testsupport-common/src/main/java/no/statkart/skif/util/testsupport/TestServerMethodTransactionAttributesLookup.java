package no.statkart.skif.util.testsupport;

import java.lang.reflect.Method;

/**
 * Hjelpeklasse for å hente ut [@code TestServerMethodTransactionAttributeType} for en metode. Annotasjonen kan
 * enten stå på selve metoden eller på klassen metoden tilhører.
 * </p>
 * Hvis ingen annotasjon er spesifisert anvendes {@code TestServerMethodTransactionAttributeType.NOT_SUPPORTED}.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class TestServerMethodTransactionAttributesLookup {
    /**
     * Default transaction attribute for testmetoder hvor det ikke er spesifisert.
     */
    private static TestServerMethodTransactionAttributeType DEFAULT_TRANSACTION_ATTRIBUTE = TestServerMethodTransactionAttributeType.NOT_SUPPORTED;

    public static TestServerMethodTransactionAttributeType getAnnotation(Method method) {
        Class<?> type = method.getDeclaringClass();
        TestServerMethodTransactionAttributeType classDefaultTxType = getAnnotationValue(type.getAnnotation(TestServerMethodTransactionAttribute.class), DEFAULT_TRANSACTION_ATTRIBUTE);
        TestServerMethodTransactionAttributeType txType = getAnnotationValue(method.getAnnotation(TestServerMethodTransactionAttribute.class), classDefaultTxType);
        return txType;
    }

    private static TestServerMethodTransactionAttributeType getAnnotationValue(TestServerMethodTransactionAttribute annotation, TestServerMethodTransactionAttributeType defaultValue) {
        if (annotation != null) {
            return annotation.value();
        } else {
            return defaultValue;
        }
    }

}
