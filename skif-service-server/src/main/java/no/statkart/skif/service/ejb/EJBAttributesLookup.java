package no.statkart.skif.service.ejb;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.LinkedKeyBinding;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.annotation.Implementation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Singleton
public class EJBAttributesLookup<S> {
    /**
     * Default transaction attribute for metoder hvor det ikke er spesifisert. I Weblogic 10.x er dette REQUIRED. I tidligere versojner var det SUPPORTS
     */
    private static TransactionAttributeType DEFAULT_TRANSACTION_ATTRIBUTE = TransactionAttributeType.REQUIRED;
    private boolean beanManagedTransaction = false;
    protected final Injector injector;
    protected final TypeLiteral<S> type;
    protected final ServiceMode serviceMode;
    private final Map<String, TransactionAttributeType> methodToTxTypeMap = new HashMap<String, TransactionAttributeType>();
    private boolean ejbCallsNotRequired = true;

    @Inject
    public EJBAttributesLookup(Injector injector, TypeLiteral<S> type, ServiceMode serviceMode) {
        this.injector = injector;
        this.type = type;
        this.serviceMode = serviceMode;
        final Class<? extends S> classToAnalyse = getClassToAnalyse(type);
        analyseClass(classToAnalyse);
    }

    public TransactionAttributeType lookupAttribute(Method m) {
        return methodToTxTypeMap.get(m.getName());
    }

    public boolean isBeanManagedTransaction() {
        return beanManagedTransaction;
    }

    public boolean isEjbCallsNotRequired() {
        return ejbCallsNotRequired;
    }

    protected Class<? extends S> getClassToAnalyse(TypeLiteral<S> type) {
        Class<? extends S> c = getEJBClass(type);
        if (c == null && serviceMode == ServiceMode.SINGLE_VM) {
            c = getImplementationClass(type);
        }
        if (c == null) {
            throw new no.statkart.skif.exception.ConfigurationException("No class for service found: " + type);
        }
        return c;
    }


    private void analyseClass(Class<? extends S> classToAnalyse) {
        TransactionManagement transactionManagement = classToAnalyse.getAnnotation(TransactionManagement.class);
        beanManagedTransaction = (transactionManagement != null) && (transactionManagement.value() == TransactionManagementType.BEAN);
        if (beanManagedTransaction){
            for (Method m : classToAnalyse.getDeclaredMethods()) {
                TransactionAttributeType txType = TransactionAttributeType.REQUIRES_NEW;
                methodToTxTypeMap.put(m.getName(), txType);
                ejbCallsNotRequired &= txType == TransactionAttributeType.SUPPORTS;
            }
        } else {
            TransactionAttributeType classDefaultTxType = getType(classToAnalyse.getAnnotation(TransactionAttribute.class), DEFAULT_TRANSACTION_ATTRIBUTE);

            for (Method m : classToAnalyse.getDeclaredMethods()) {
                TransactionAttributeType txType = getType(m.getAnnotation(TransactionAttribute.class), classDefaultTxType);
                methodToTxTypeMap.put(m.getName(), txType);
                ejbCallsNotRequired &= txType == TransactionAttributeType.SUPPORTS;
            }
        }
    }

    private TransactionAttributeType getType(TransactionAttribute annotation, TransactionAttributeType defaultTxType) {
        if (annotation != null) {
            return annotation.value();
        } else {
            return defaultTxType;
        }
    }

    private Class<? extends S> getEJBClass(TypeLiteral<S> type) {
        if (type.getRawType().getAnnotation(Stateless.class) != null) return (Class<? extends S>) type.getRawType();

        try {
            return SkifUtil.classForName(type.getRawType().getName() + "EJBBean");
        } catch (RuntimeException e) {
            // Fant ingen EJBKlasse
            return null;

        }
    }

    private Class<? extends S> getImplementationClass(TypeLiteral<S> type) {
        final Binding<S> binding = injector.getBinding(Key.get(type, Implementation.class));
        if (binding instanceof LinkedKeyBinding) {
            LinkedKeyBinding<S> linkedKeyBinding = (LinkedKeyBinding<S>) binding;
            return (Class<? extends S>) linkedKeyBinding.getLinkedKey().getTypeLiteral().getRawType();
        }
        return null;
    }

    public Set<Map.Entry<String, TransactionAttributeType>> getTransactionAttributeTypes() {
        return methodToTxTypeMap.entrySet();

    }
}
