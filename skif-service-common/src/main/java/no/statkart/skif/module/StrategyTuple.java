package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import static no.statkart.skif.SkifConstants.*;

/**
 * En tuple som kan definere en {@code ModuleStrategy}-instans av type {@code T} for hver {@code ServiceMode}-verdi. Hver
 * {@ModuleStrategy}-instans kan enten være definert direkte via en instans eller indirekte via en klasse av type
 * {@code Class<? extends T>} eller via et klassenavn. Dersom hverken instansen eller klasse er definert for en gitt
 * {@code ServiceMode}-verdi returneres null.
 * <p>
 * Det er mulig å lage en kopi av en {@code StrategyTuple}-instans via kall til {@link #clone()}. Endringer gjort
 * på det klonet instansen vil ikke påvirke den opprindelige instansen.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StrategyTuple<T extends ModuleStrategy> implements Cloneable {
    protected T instanceJEE;
    protected T instanceSingleVm;
    protected String strategyJEEClassName;
    protected String strategySingleVmClassName;
    protected Configuration configuration;

    public StrategyTuple() {
    }

    void setConfiguration(Configuration configuration) {
        Preconditions.checkNotNull(configuration, "configuration");
        this.configuration = configuration;
        if (instanceJEE!=null) {
            instanceJEE.setProperties(configuration);
        }
        if (instanceSingleVm!=null) {
            instanceSingleVm.setProperties(configuration);
        }
    }

    public StrategyTuple(@Nullable T instanceJEE, @Nullable T instanceSingleVm) {
        setInstanceJEE(instanceJEE);
        setInstanceSingleVm(instanceSingleVm);
    }

    public StrategyTuple(@Nullable Class<? extends T> strategyJEEClass, @Nullable Class<? extends T> strategySingleVmClass) {
        String strategyJEEClassName = strategyJEEClass==null ? null : strategyJEEClass.getName();
        String strategySingleVmClassName = strategySingleVmClass==null ? null : strategySingleVmClass.getName();
        setStrategySingleVmClassName(strategySingleVmClassName);
        setStrategyJEEClassName(strategyJEEClassName);
    }

    public StrategyTuple(@Nullable String strategyJEEClassName, @Nullable String strategySingleVmClassName) {
        setStrategySingleVmClassName(strategySingleVmClassName);
        setStrategyJEEClassName(strategyJEEClassName);
    }

    public StrategyTuple(Class<? extends T> strategyBaseClass) {
        Preconditions.checkNotNull(strategyBaseClass, "strategyBaseClass");
        setStrategyJEEClassName(strategyBaseClass.getName() + JEE_POSTFIX);
        setStrategySingleVmClassName(strategyBaseClass.getName() + SINGEL_VM_POSTFIX);
    }

    public StrategyTuple(String strategyBaseClassName) {
        Preconditions.checkNotNull(strategyBaseClassName, "strategyBaseClassName");
        setStrategyJEEClassName(strategyBaseClassName + JEE_POSTFIX);
        setStrategySingleVmClassName(strategyBaseClassName + SINGEL_VM_POSTFIX);

    }

    @Nullable
    protected String getStrategyJEEClassName() {
        return strategyJEEClassName;
    }

    protected void setStrategyJEEClassName(@Nullable String strategyJEEClassName) {
        Preconditions.checkArgument(instanceJEE ==null || instanceJEE.getClass().getName().equals(strategyJEEClassName));
        this.strategyJEEClassName = strategyJEEClassName;
    }

    protected void setStrategyJEEClass(@Nullable Class<? extends T> stragetyJEEClass) {
        if (stragetyJEEClass==null) {
            setStrategyJEEClassName(null);
        } else {
            setStrategyJEEClassName(stragetyJEEClass.getName());
        }
    }

    @Nullable
    protected String getStrategySingleVmClassName() {
        return strategySingleVmClassName;
    }

    protected void setStragetySingleVmClass(@Nullable Class<? extends T> stragetySingleVmClass) {
        if (stragetySingleVmClass==null) {
            setStrategySingleVmClassName(null);
        } else {
            setStrategySingleVmClassName(stragetySingleVmClass.getName());
        }
    }

    protected void setStrategySingleVmClassName(@Nullable String strategySingleVmClassName) {
        Preconditions.checkArgument(instanceSingleVm ==null || instanceSingleVm.getClass().getName().equals(strategySingleVmClassName));
        this.strategySingleVmClassName = strategySingleVmClassName;
    }

    @Nullable
    protected T getInstanceSingleVm() {
        if (instanceSingleVm == null) {
            instanceSingleVm = createInstanceSingleVm();
        }
        return instanceSingleVm;
    }

    protected void setInstanceJEE(@Nullable T instanceJEE) {
        this.instanceJEE = instanceJEE;
        if (instanceJEE != null) {
            this.strategyJEEClassName = instanceJEE.getClass().getName();
        }
    }

    protected void setInstanceSingleVm(@Nullable T instanceSingleVm) {
        this.instanceSingleVm = instanceSingleVm;
        if (instanceSingleVm != null) {
            this.strategySingleVmClassName = instanceSingleVm.getClass().getName();
        }
    }

    @Nullable
    protected final T getInstanceJEE() {
        if (instanceJEE == null) {
            instanceJEE = createInstanceJEE();
        }
        return instanceJEE;
    }

    @Nullable
    protected T createInstanceJEE() {
        T instance = null;
        if (strategyJEEClassName !=null) {
            instance = (T)SkifUtil.newInstance(strategyJEEClassName);
            if (configuration !=null) {
                instance.setProperties(configuration);
            }
        }
        return instance;
    }

    @Nullable
    protected T createInstanceSingleVm() {
        T instance = null;
        if (strategySingleVmClassName !=null) {
            instance = (T)SkifUtil.newInstance(strategySingleVmClassName);
            if (configuration !=null) {
                instance.setProperties(configuration);
            }
        }
        return instance;
    }


    @Nullable
    public T getStrategy(ServiceMode serviceMode) {
        final T strategy;
        if (serviceMode == ServiceMode.SINGLE_VM) {
            strategy = (T) getInstanceSingleVm();
        } else {
            strategy = (T) getInstanceJEE();
        }
        return strategy;
    }

    public void setStrategy(ServiceMode serviceMode, T strategy) {
        if (serviceMode == ServiceMode.SINGLE_VM) {
            setInstanceSingleVm(strategy);
        } else {
            setInstanceJEE(strategy);
        }
    }

    @Nullable
    public String getStrategyClassName(ServiceMode serviceMode) {
        String className;
        if (serviceMode==ServiceMode.SINGLE_VM) {
            className = getStrategySingleVmClassName();
        } else  {
            className = getStrategyJEEClassName();
        }
        return className;
    }

    public void setStrategyClassName(ServiceMode serviceMode, String strategyClassName) {
        if (serviceMode==ServiceMode.SINGLE_VM) {
            setStrategySingleVmClassName(strategyClassName);
        } else  {
            setStrategyJEEClassName(strategyClassName);
        }
    }

    @Nullable
    public Class<? extends T> getStrategyClass(ServiceMode serviceMode) {
        String className;
        if (serviceMode==ServiceMode.SINGLE_VM) {
            className = getStrategySingleVmClassName();
        } else  {
            className = getStrategyJEEClassName();
        }
        return (Class<? extends T>) (className==null ? null : SkifUtil.classForName(className));
    }

    public void setStrategyClass(ServiceMode serviceMode, Class<? extends T> strategyClass) {
        String className = strategyClass==null ? null : strategyClass.getName();
        if (serviceMode==ServiceMode.SINGLE_VM) {
            setStrategySingleVmClassName(className);
        } else  {
            setStrategyJEEClassName(className);
        }
    }

    @Override
    public StrategyTuple<T> clone()  {
        try {
            final StrategyTuple<T> clone = (StrategyTuple<T>) super.clone();
            if (clone.instanceJEE!=null) {
                clone.instanceJEE = (T)instanceJEE.clone();
            }
            if (clone.instanceSingleVm !=null) {
                clone.instanceSingleVm = (T)instanceSingleVm.clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
