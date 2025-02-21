package no.statkart.skif.util.testsupport;

import java.lang.reflect.Method;

/**
 * Hjelpeklasse for å hente ut [@code TestTransactionAttributeType} for en metode. Annotasjonen kan
 * enten stå på selve metoden eller på klassen metoden tilhører.
 * </p>
 * Hvis ingen annotasjon er spesifisert anvendes {@code TestServerMethodTransactionAttributeType.TX_REQUIRED}.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class TestTransactionAttributesLookup {
    /**
     * Default transaction attribute for testmetoder hvor det ikke er spesifisert.
     */
    private static TestTransactionAttributeType DEFAULT_TRANSACTION_ATTRIBUTE = TestTransactionAttributeType.TX_REQUIRED;

    public static TestTransactionAttributeType getAnnotation(Method method) {
        Class<?> type = method.getDeclaringClass();
        TestTransactionAttributeType classDefaultTxType = getAnnotationValue(type.getAnnotation(TestTransactionAttribute.class), DEFAULT_TRANSACTION_ATTRIBUTE);
        TestTransactionAttributeType txType = getAnnotationValue(method.getAnnotation(TestTransactionAttribute.class), classDefaultTxType);
        return txType;
    }

    private static TestTransactionAttributeType getAnnotationValue(TestTransactionAttribute annotation, TestTransactionAttributeType defaultValue) {
        if (annotation != null) {
            return annotation.value();
        } else {
            return defaultValue;
        }
    }

}
