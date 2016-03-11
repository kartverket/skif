package no.statkart.skif.module;

import com.google.common.base.Preconditions;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import static no.statkart.skif.SkifConstants.JEE_POSTFIX;
import static no.statkart.skif.SkifConstants.SINGLE_VM_POSTFIX;
import static no.statkart.skif.SkifConstants.SINGLE_VM_XML_POSTFIX;

/**
 * En tuple som kan definere en {@link ModuleStrategy}-instans av type {@code T} for hver {@link ServiceMode}-verdi.
 * Hver {@code ModuleStrategy}-instans kan enten være definert direkte via en instans eller indirekte via en klasse av
 * type {@code Class&lt;? extends T&gt;} eller via et klassenavn. Dersom hverken instansen eller klasse er definert for
 * en gitt {@code ServiceMode}-verdi returneres {@code null}.
 * <p>
 * Det er mulig å lage en kopi av en {@code StrategyTuple}-instans via kall til {@link #clone()}. Endringer gjort
 * på den klonede instansen vil ikke påvirke den opprindelige instansen.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand (flere enn to ServiceMode)
 * @since 2.0
 */
public class StrategyTuple<T extends ModuleStrategy> implements Cloneable {
    protected EnumMap<ServiceMode, T> instanceForMode = new EnumMap<>(ServiceMode.class);
    protected EnumMap<ServiceMode, String> strategyClassNameForMode = new EnumMap<>(ServiceMode.class);
    protected Configuration configuration;

    public StrategyTuple() {
    }

    void setConfiguration(Configuration configuration) {
        Preconditions.checkNotNull(configuration, "configuration");
        this.configuration = configuration;
        for (T instance : instanceForMode.values()) {
            if (instance != null) {
                instance.setProperties(configuration);
            }
        }
    }

    public StrategyTuple(@Nullable T instanceJEE, @Nullable T instanceSingleVm) {
        setStrategy(ServiceMode.JEE, instanceJEE);
        setStrategy(ServiceMode.SINGLE_VM, instanceSingleVm);
    }

    public StrategyTuple(@Nullable Class<? extends T> strategyJEEClass, @Nullable Class<? extends T> strategySingleVmClass) {
        String strategyJEEClassName = strategyJEEClass == null ? null : strategyJEEClass.getName();
        String strategySingleVmClassName = strategySingleVmClass == null ? null : strategySingleVmClass.getName();
        setStrategyClassName(ServiceMode.JEE, strategyJEEClassName);
        setStrategyClassName(ServiceMode.SINGLE_VM, strategySingleVmClassName);
    }

    public StrategyTuple(@Nullable Class<? extends T> strategyJEEClass, @Nullable Class<? extends T> strategySingleVmClass, @Nullable Class<? extends T> strategySingleVmXmlClass) {
        String strategyJEEClassName = strategyJEEClass == null ? null : strategyJEEClass.getName();
        String strategySingleVmClassName = strategySingleVmClass == null ? null : strategySingleVmClass.getName();
        String strategySingleVmXmlClassName = strategySingleVmXmlClass == null ? null : strategySingleVmXmlClass.getName();
        setStrategyClassName(ServiceMode.JEE, strategyJEEClassName);
        setStrategyClassName(ServiceMode.SINGLE_VM, strategySingleVmClassName);
        setStrategyClassName(ServiceMode.SINGLE_VM_XML, strategySingleVmXmlClassName);
    }

    public StrategyTuple(@Nullable String strategyJEEClassName, @Nullable String strategySingleVmClassName) {
        setStrategyClassName(ServiceMode.JEE, strategyJEEClassName);
        setStrategyClassName(ServiceMode.SINGLE_VM, strategySingleVmClassName);
    }

    public StrategyTuple(@Nullable String strategyJEEClassName, @Nullable String strategySingleVmClassName, @Nullable String strategySingleVmXmlClassName) {
        setStrategyClassName(ServiceMode.JEE, strategyJEEClassName);
        setStrategyClassName(ServiceMode.SINGLE_VM, strategySingleVmClassName);
        setStrategyClassName(ServiceMode.SINGLE_VM_XML, strategySingleVmXmlClassName);
    }

    public StrategyTuple(Class<? extends T> strategyBaseClass) {
        Preconditions.checkNotNull(strategyBaseClass, "strategyBaseClass");
        setStrategyClassName(ServiceMode.JEE, strategyBaseClass.getName() + JEE_POSTFIX);
        setStrategyClassName(ServiceMode.SINGLE_VM, strategyBaseClass.getName() + SINGLE_VM_POSTFIX);
        setStrategyClassName(ServiceMode.SINGLE_VM_XML, strategyBaseClass.getName() + SINGLE_VM_XML_POSTFIX);
    }

    public StrategyTuple(String strategyBaseClassName) {
        Preconditions.checkNotNull(strategyBaseClassName, "strategyBaseClassName");
        setStrategyClassName(ServiceMode.JEE, strategyBaseClassName + JEE_POSTFIX);
        setStrategyClassName(ServiceMode.SINGLE_VM, strategyBaseClassName + SINGLE_VM_POSTFIX);
        setStrategyClassName(ServiceMode.SINGLE_VM_XML, strategyBaseClassName + SINGLE_VM_XML_POSTFIX);
    }

    @Nullable
    protected T createInstance(ServiceMode serviceMode) {
        T instance = null;
        String strategyClassName = strategyClassNameForMode.get(serviceMode);
        if (strategyClassName != null) {
            instance = SkifUtil.newInstance(strategyClassName);
            if (configuration != null) {
                instance.setProperties(configuration);
            }
        }
        return instance;
    }


    @Nullable
    public T getStrategy(ServiceMode serviceMode) {
        T instance = instanceForMode.get(serviceMode);

        if (instance == null) {
            instance = createInstance(serviceMode);
            instanceForMode.put(serviceMode, instance);
        }

        return instance;
    }

    public void setStrategy(ServiceMode serviceMode, T strategy) {
        instanceForMode.put(serviceMode, strategy);
        if (strategy != null) {
            strategyClassNameForMode.put(serviceMode, strategy.getClass().getName());
        }
    }

    @Nullable
    public String getStrategyClassName(ServiceMode serviceMode) {
        //noinspection UnnecessaryLocalVariable
        String className = strategyClassNameForMode.get(serviceMode);
        return className;
    }

    public void setStrategyClassName(ServiceMode serviceMode, String strategyClassName) {
        T instance = instanceForMode.get(serviceMode);
        Preconditions.checkArgument(instance == null || instance.getClass().getName().equals(strategyClassName));
        strategyClassNameForMode.put(serviceMode, strategyClassName);
    }

    @Nullable
    public Class<? extends T> getStrategyClass(ServiceMode serviceMode) {
        String className = getStrategyClassName(serviceMode);
        return className == null ? null : SkifUtil.<T>classForName(className);
    }

    public void setStrategyClass(ServiceMode serviceMode, Class<? extends T> strategyClass) {
        String className = strategyClass == null ? null : strategyClass.getName();
        setStrategyClassName(serviceMode, className);
    }

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException", "unchecked"})
    @Override
    public StrategyTuple<T> clone() {
        try {
            final StrategyTuple<T> clone = (StrategyTuple<T>) super.clone();
            clone.strategyClassNameForMode = strategyClassNameForMode.clone();
            clone.instanceForMode = new EnumMap<ServiceMode, T>(ServiceMode.class);
            for (Map.Entry<ServiceMode, T> entry : instanceForMode.entrySet()) {
                clone.instanceForMode.put(entry.getKey(), (T) entry.getValue().clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
