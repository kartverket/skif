package no.statkart.skif.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;

import static no.statkart.skif.config.SkifConfigConstants.*;

import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.*;
import no.statkart.skif.internal.util.InternalConfigurationUtils;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class ModuleBuilder {
    private static Logger logger = LoggerFactory.getLogger(ModuleBuilder.class);

    /**
     * Configuration instans som inneholder properties som settes programatisk på builderen. Hver module
     * som builderen produserer får sin egen kopi av disse properties slik at senere endringer av disse properties
     * ikke påvirker allerede produserte moduler. Properties satt programatisk kan ikke overstyres via systemproperties.
     * Properties'ene blir ikke nullstilt automatisk for hvergang en module produseres, men beholder deres verdier.
     * Det er mulig å nullstille dem manuelt.
     */
    private MapConfiguration builderConfiguration;

    /**
     * Configuration instans som inneholder systemproperties og som overstyrer alle andre properties på nær de som har blitt
     * satt programatisk direkte på builderen (dvs. <properties i {@code builderConfiguration}}. SystemConfiguration
     * brukes av alle moduler som produseres av builderen (inkl. singleVmServer-moduler) dersom den settes i
     * constructoren når builderen opprettes. Moduler som deler Configuration instans bruker samme SystemConfiguration
     * instans. Moduler som ikke deler Configuration instans bruker hver sin SystemConfiguration kopi. SingleVmServer
     * moduler deler ikke Configuration instans med klienten har bruker derfor sin egen SystemConfiguration instans.
     * @deprecated Bruk SkifConfiguration til konfigurasjonshierarki
     */
    protected SystemConfiguration systemConfiguration;

    /**
     * Configration som inneholder properties for moduler som builderen skal produsere.
     * Dersom builderen brukes til å produsere flere moduler vil configuration-instansen deles av alle moduler som blir produsert
     * inntil en ny configuration settes. Denne instansen brukes ikke for singleVmServer-moduler
     */
    private Configuration configuration;

    /**
     * Konfigurasjonsproperties som inneholder properties for singleVmServer-moduler som builderen  produsere.
     */
    private Configuration singleVmServerConfiguration;


    /**
     * StrategyFactory instans som modulen skal bruke dersom feltet er satt. Hvis feltet er null vil builderen
     * opprette ModuleStrategyFactory instans basert på hvilken ModuleStrategyFactoryClass som er spesifisert.
     */
    private ModuleStrategyFactory moduleStrategyFactory;

    /**
     * StrategyFactory instans som singleVmServer-modulen skal bruke dersom feltet er satt. Hvis feltet er null vil builderen
     * opprette ModuleStrategyFactory instans basert på hvilken SingleVmServerModuleStrategyFactoryClass som er spesifisert.
     */
    private ModuleStrategyFactory singleVmServerModuleStrategyFactory;

    /**
     * Delt singleVmServer instans som anvendes dersom useSharedServer er true
     */
    protected Injector singleVmServerInjector;


    /**
     * Configuration som kombinerer alle de andre configurations. Initialiseres lazy.
     * @deprecated Bruk SkifConfiguration til konfigurasjonshierarki
     */
    private CompositeConfiguration compositeConfiguration;

    public ModuleBuilder() {
        this(null);
    }

    /**
     * @deprecated Bruk SkifConfiguration til konfigurasjonshierarki
     * @param systemConfiguration systemkonfigurasjon
     */
    public ModuleBuilder(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
        this.builderConfiguration = new MapConfiguration();
        this.configuration = new MapConfiguration();
        this.singleVmServerConfiguration = new MapConfiguration();
    }

    /**
     * Oppretten CompositeConfiguration on demand. Hvergang en ny configuration settes på builderen må
     * {@code composisteConfiguration} nullstilles slik at det blir laget en ny compositeConfigurasjon
     * som bruker en egen kopi av {@code systemConfiguration}. Ved å gjøre access til {@code compositeConfiguration}
     * lazy unngås unødig kopiering av {@code systemConfiguration}.
     */
    private CompositeConfiguration getCompositeConfiguration() {
        if (compositeConfiguration == null) {
            this.compositeConfiguration = new CompositeConfiguration();
            this.compositeConfiguration.addConfiguration(builderConfiguration);
            if (systemConfiguration != null) {
                this.compositeConfiguration.addConfiguration(InternalConfigurationUtils.cloneConfiguration(systemConfiguration));
            }
            this.configuration = internalGetConfiguration();
            this.compositeConfiguration.addConfiguration(configuration);
        }
        return compositeConfiguration;

    }

    public MapConfiguration getBuilderConfiguration() {
        return builderConfiguration;
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
            final String filename = getConfigurationFilename();
            if (filename != null) {
                setConfiguration(new PropertiesConfiguration(filename));
            } else {
                setConfiguration(internalGetConfiguration());
            }

        }
        return configuration;
    }

    private Configuration internalGetConfiguration() {
        if (configuration != null) {
            return configuration;
        } else {
            final String filename = getConfigurationFilename();
            if (filename != null) {
                return new PropertiesConfiguration(filename);
            } else {
                return new MapConfiguration();
            }
        }
    }

    public ModuleBuilder setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        compositeConfiguration = null;
        return this;
    }

    /**
     * @deprecated Bruk SkifConfiguration til konfigurasjonshierarki
     * @return konfigurasjonsfilnavn
     */
    public String getConfigurationFilename() {
        return getCompositeConfiguration().getString(CONFIGURATION_FILENAME);
    }

    /**
     * @deprecated Bruk SkifConfiguration til konfigurasjonshierarki
     * @param filename navn på konfigurasjonsfil
     * @return <code>this</code>
     */
    public ModuleBuilder setConfigurationFilename(String filename) {
        builderConfiguration.setProperty(CONFIGURATION_FILENAME, filename);
        setConfiguration(null);
        return this;
    }

    public Configuration getSingleVmServerConfiguration() {
        if (singleVmServerConfiguration == null) {
            final String filename = getSingleVmServerConfigurationFilename();
            if (filename != null) {
                setSingleVmServerConfiguration(new PropertiesConfiguration(filename));
            }
        }
        return singleVmServerConfiguration;
    }

    public ModuleBuilder setSingleVmServerConfiguration(Configuration configuration) {
        singleVmServerInjector = null;
        this.singleVmServerConfiguration = configuration;
        return this;
    }

    public String getSingleVmServerConfigurationFilename() {
        return getCompositeConfiguration().getString(SINGLE_VM_SERVER_CONFIGURATION_FILENAME);
    }

    public ModuleBuilder setSingleVmServerConfigurationFilename(String filename) {
        singleVmServerInjector = null;
        singleVmServerConfiguration = null;
        builderConfiguration.setProperty(SINGLE_VM_SERVER_CONFIGURATION_FILENAME, filename);
        return this;
    }

    /**
     * Angir om moduler skal bruke samme singleVmServer instans når modulenes singlevmServer konfigurasjon er uendret
     */
    public boolean isUseSharedServer() {
        return getCompositeConfiguration().getBoolean(USE_SHARED_SERVER, true);
    }

    public ModuleBuilder setUseSharedServer(boolean useSharedServer) {
        builderConfiguration.setProperty(USE_SHARED_SERVER, useSharedServer);
        return this;
    }

    public ModuleStrategyFactory getSingleVmServerModuleStrategyFactory() {
        return singleVmServerModuleStrategyFactory;
    }

    public void setSingleVmServerModuleStrategyFactory(ModuleStrategyFactory singleVmServerModuleStrategyFactory) {
        this.singleVmServerModuleStrategyFactory = singleVmServerModuleStrategyFactory;
    }


    public ServiceMode getServiceMode() {
        return getCompositeConfiguration().getBoolean(SINGLE_VM, false) ? ServiceMode.SINGLE_VM : ServiceMode.JEE;
    }

    public ModuleBuilder setServiceMode(ServiceMode serviceMode) {
        builderConfiguration.setProperty(SINGLE_VM, String.valueOf(serviceMode == ServiceMode.SINGLE_VM));
        return this;
    }

    public ModuleBuilder setSingleVm(boolean isSingleVm) {
        builderConfiguration.setProperty(SINGLE_VM, isSingleVm);
        return this;
    }

    public String getModuleClassname() {
        return getCompositeConfiguration().getString(MODULE_CLASS);
    }

    public ModuleBuilder setModuleClassname(String moduleClassname) {
        builderConfiguration.setProperty(MODULE_CLASS, moduleClassname);
        return this;
    }

    public ModuleBuilder setModuleClass(Class<? extends Module> moduleClass) {
        setModuleClassname(moduleClass.getName());
        return this;
    }

    public String getModuleExtClassname() {
        return getCompositeConfiguration().getString(MODULE_EXT_CLASS);
    }

    public ModuleBuilder setModuleExtClassname(String moduleClassname) {
        builderConfiguration.setProperty(MODULE_EXT_CLASS, moduleClassname);
        return this;
    }

    public ModuleBuilder setModuleExtClass(Class<? extends Module> moduleClass) {
        setModuleExtClassname(moduleClass.getName());
        return this;
    }

    public ModuleStrategyFactory getModuleStrategyFactory() {
        return moduleStrategyFactory;
    }

    public void setModuleStrategyFactory(ModuleStrategyFactory moduleStrategyFactory) {
        this.moduleStrategyFactory = moduleStrategyFactory;
    }

    public String getModuleStrategyFactoryClassname() {
        return getCompositeConfiguration().getString(MODULE_STRATEGY_FACTORY_CLASS);
    }

    public ModuleBuilder setModuleStrategyFactoryClassname(String moduleStrategyFactoryClassname) {
        builderConfiguration.setProperty(MODULE_STRATEGY_FACTORY_CLASS, moduleStrategyFactoryClassname);
        return this;
    }

    public ModuleBuilder setModuleStrategyFactoryClass(Class<? extends ModuleStrategyFactory> moduleStrategyFactory) {
        setModuleStrategyFactoryClassname(moduleStrategyFactory.getName());
        return this;
    }

    public String getEjbServiceChainExtClassname() {
        return builderConfiguration.getString(EJB_SERVICE_CHAIN_EXT_CLASS);
    }

    public ModuleBuilder setEjbServiceChainExtClass(Class<? extends ChainedProxyHandler> ejbServiceChainExtClass) {
        setSingleVmServerEjbServiceChainExtClassname(ejbServiceChainExtClass.getName());
        return this;
    }

    public ModuleBuilder setEjbServiceChainExtClassname(String ejbServiceChainExtClassname) {
        builderConfiguration.setProperty(EJB_SERVICE_CHAIN_EXT_CLASS, ejbServiceChainExtClassname);
        return this;
    }

    public String getSingleVmServerModuleClassname() {
        return getCompositeConfiguration().getString(SINGLE_VM_SERVER_MODULE_CLASS);
    }

    public ModuleBuilder setSingleVmServerModuleClassname(String singleVmServerModuleClassname) {
        builderConfiguration.setProperty(SINGLE_VM_SERVER_MODULE_CLASS, singleVmServerModuleClassname);
        return this;
    }

    public ModuleBuilder setSingleVmServerModuleClass(Class<? extends Module> singleVmServerModuleClass) {
        setSingleVmServerModuleClassname(singleVmServerModuleClass.getName());
        return this;
    }

    public String getSingleVmServerModuleExtClassname() {
        return getCompositeConfiguration().getString(SINGLE_VM_SERVER_MODULE_EXT_CLASS);
    }

    public ModuleBuilder setSingleVmServerModuleExtClassname(String singleVmServerModuleClassname) {
        builderConfiguration.setProperty(SINGLE_VM_SERVER_MODULE_EXT_CLASS, singleVmServerModuleClassname);
        return this;
    }

    public ModuleBuilder setSingleVmServerModuleExtClass(Class<? extends Module> singleVmServerModuleClass) {
        setSingleVmServerModuleExtClassname(singleVmServerModuleClass.getName());
        return this;
    }

    public String getSingleVmServerEjbServiceChainExtClassname() {
        return builderConfiguration.getString(SINGLE_VM_SERVER_EJB_SERVICE_CHAIN_EXT_CLASS);
    }

    public ModuleBuilder setSingleVmServerEjbServiceChainExtClass(Class<? extends ChainedProxyHandler> ejbServiceChainExtClass) {
        setSingleVmServerEjbServiceChainExtClassname(ejbServiceChainExtClass.getName());
        return this;
    }

    public ModuleBuilder setSingleVmServerEjbServiceChainExtClassname(String ejbServiceChainExtClassname) {
        builderConfiguration.setProperty(SINGLE_VM_SERVER_EJB_SERVICE_CHAIN_EXT_CLASS, ejbServiceChainExtClassname);
        return this;
    }

    public String getSingleVmServerModuleStrategyFactoryClassname() {
        return getCompositeConfiguration().getString(SINGLE_VM_SERVER_MODULE_STRATEGY_FACTORY_CLASS);
    }


    public ModuleBuilder setSingleVmServerModuleStrategyFactoryClassname(String singleVmServerModuleStrategyFactoryClassname) {
        builderConfiguration.setProperty(SINGLE_VM_SERVER_MODULE_STRATEGY_FACTORY_CLASS, singleVmServerModuleStrategyFactoryClassname);
        return this;
    }

    public ModuleBuilder setSingleVmServerModuleStrategyFactoryClass(Class<? extends ModuleStrategyFactory> singleVmServerModuleStrategyFactoryClass) {
        setSingleVmServerModuleStrategyFactoryClassname(singleVmServerModuleStrategyFactoryClass.getName());
        return this;
    }

    public Module buildModule() {
        String moduleClassname = getModuleClassname();
        Preconditions.checkNotNull(getModuleClassname(), "ModuleClassname");
        Preconditions.checkArgument(getModuleExtClassname() == null, "ModuleExtClassname er satt. Bruk buildModules()");
        Constructor<? extends SkifModule> constructor = getModuleConstructor(moduleClassname);
        final Module module = buildModule(constructor);
        return module;

    }

    public List<Module> buildModules() {
        List<Module> modules = new ArrayList<Module>();
        String moduleClassname = getModuleClassname();
        Preconditions.checkNotNull(getModuleClassname(), "ModuleClassname");
        Constructor<? extends SkifModule> constructor = getModuleConstructor(moduleClassname);
        modules.add(buildModule(constructor));
        final String moduleExtClassname = getModuleExtClassname();
        if (moduleExtClassname!= null) {
            constructor = getModuleConstructor(moduleExtClassname);
            modules.add(buildModule(constructor));
        }
        return modules;
    }

    private Module buildModule(Constructor<? extends SkifModule> constructor) {
        SkifModule module;
        Object constructorParameter;
        if (constructorIsUsingModuleConfigurationParameter(constructor)) {
            constructorParameter = createModuleConfigurationForModule();
        } else {
            constructorParameter = createConfigurationForModule();
        }

        try {
            module = constructor.newInstance(constructorParameter);
        } catch (InstantiationException e) {
            throw new ImplementationException("Could not instantiate module", e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Could not instantiate module", e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException("Could not instantiate module", e);
        }

        return module;
    }

    private Configuration createConfigurationForModule() {
        StackedConfiguration c = new StackedConfiguration((MapConfiguration) builderConfiguration.clone());
        if (systemConfiguration != null) {
            c.addConfiguration(systemConfiguration);
        }
        if (isSingleVmModuleRequired()) {
            Injector singleVmServerInjector = getSingleVmServerInjector();
            c.setProperty(SINGLE_VM_SERVER_INJECTOR, singleVmServerInjector);
        }
        c.addConfiguration(getConfiguration());
        return c;
    }

    private boolean isSingleVmModuleRequired() {
        return getServiceMode() == ServiceMode.SINGLE_VM && getCompositeConfiguration().containsKey(SINGLE_VM_SERVER_MODULE_CLASS);
    }

    private ModuleConfiguration createModuleConfigurationForModule() {
        // Lag en kopi av compo
        CompositeConfiguration c = createCompositeConfigurationForModule();
        ModuleConfiguration moduleConfiguration = new DefaultModuleConfiguration(c, moduleStrategyFactory);
        if (getServiceMode() == ServiceMode.SINGLE_VM && getSingleVmServerModuleClassname() != null) {
            Injector singleVmServerInjector = getSingleVmServerInjector();
            moduleConfiguration.getConfiguration().setProperty(SkifConfigConstants.SINGLE_VM_SERVER_INJECTOR, singleVmServerInjector);
        }
        return moduleConfiguration;
    }

    /**
     * Lager en klon av {@code compositeConfiguration} som innenholder en kopi av {@code builderconfiguration} mens
     * alle de andre configurations er bevart, slik at modulene produsert av builderen kan dele konfigurasjon.
     */
    private CompositeConfiguration createCompositeConfigurationForModule() {
        final CompositeConfiguration compositeConfiguration = getCompositeConfiguration();
        CompositeConfiguration c = new CompositeConfiguration(compositeConfiguration.getInMemoryConfiguration());
        c.addConfiguration(InternalConfigurationUtils.cloneConfiguration(builderConfiguration));
        final int numberOfConfigurations = getCompositeConfiguration().getNumberOfConfigurations();
        for (int i = 1; i < numberOfConfigurations - 1; i++) {
            c.addConfiguration(getCompositeConfiguration().getConfiguration(i));
        }
        return c;
    }

    @Deprecated
    public <T extends SkifModule> Constructor<T> getModuleConstructor() {
        String moduleClassname = getModuleClassname();
        Preconditions.checkNotNull(getModuleClassname(), "ModuleClassname");
        return getModuleConstructor(moduleClassname);
    }

    private <T extends SkifModule> Constructor<T> getModuleConstructor(String moduleClassname) {
        Class<T> moduleClass = (Class<T>) SkifUtil.classForName(moduleClassname);
        Constructor<T> constructor;
        try {
            constructor = moduleClass.getConstructor(ModuleConfiguration.class);
            return constructor;
        } catch (NoSuchMethodException ignored) {
        }

        try {
            constructor = moduleClass.getConstructor(Configuration.class);
            return constructor;
        } catch (NoSuchMethodException ignored) {
        }

        throw new ImplementationException("Could not find suitable constructor for class: " + moduleClass.getName());
    }

    private boolean constructorIsUsingModuleConfigurationParameter(Constructor<? extends SkifModule> constructor) {
        return constructor.getParameterTypes()[0] == ModuleConfiguration.class;
    }

    public Injector buildInjector() {
        return Guice.createInjector(buildModules());
    }

    public Injector getSingleVmServerInjector() {
        if (!isUseSharedServer()) singleVmServerInjector = null;
        if (singleVmServerInjector == null) {
            SystemConfiguration singleVmServerSystemConfiguration = (SystemConfiguration) InternalConfigurationUtils.cloneConfiguration(systemConfiguration);
            if (singleVmServerSystemConfiguration != null) {
                singleVmServerSystemConfiguration.clearProperty(SINGLE_VM_SERVER_MODULE_CLASS);
                singleVmServerSystemConfiguration.clearProperty(SINGLE_VM_SERVER_MODULE_EXT_CLASS);
                singleVmServerSystemConfiguration.clearProperty(SINGLE_VM_SERVER_MODULE_STRATEGY_FACTORY_CLASS);
                singleVmServerSystemConfiguration.clearProperty((SINGLE_VM_SERVER_CONFIGURATION_FILENAME));
                singleVmServerSystemConfiguration.clearProperty((SINGLE_VM_SERVER_EJB_SERVICE_CHAIN_EXT_CLASS));
            }
            ModuleBuilder singleVmServerModuleBuilder = new ModuleBuilder(singleVmServerSystemConfiguration);

            // Sett builder spesifikke properties på singleVmServerModuleBuilder fra denne builder
            singleVmServerModuleBuilder.setServiceMode(ServiceMode.SINGLE_VM);
            String moduleClassname = getSingleVmServerModuleClassname();
            if (moduleClassname != null) {
                singleVmServerModuleBuilder.setModuleClassname(moduleClassname);
            }

            String moduleExtClassname = getSingleVmServerModuleExtClassname();
            if (moduleExtClassname != null) {
                singleVmServerModuleBuilder.setModuleExtClassname(moduleExtClassname);
            }

            String moduleStrategyFactoryClassname = getSingleVmServerModuleStrategyFactoryClassname();
            if (moduleStrategyFactoryClassname != null) {
                singleVmServerModuleBuilder.setModuleStrategyFactoryClassname(moduleStrategyFactoryClassname);
            }

            ModuleStrategyFactory moduleStrategyFactory = getSingleVmServerModuleStrategyFactory();
            if (moduleStrategyFactory != null) {
                singleVmServerModuleBuilder.setModuleStrategyFactory(moduleStrategyFactory);
            }

            String ejbServiceChainExtClassname = getSingleVmServerEjbServiceChainExtClassname();
            if (ejbServiceChainExtClassname != null) {
                singleVmServerModuleBuilder.setEjbServiceChainExtClassname(ejbServiceChainExtClassname);
            }

            Configuration configuration = getSingleVmServerConfiguration();
            if (configuration != null) {
                singleVmServerModuleBuilder.setConfiguration(configuration);
            }
            singleVmServerInjector = singleVmServerModuleBuilder.buildInjector();
        }
        return singleVmServerInjector;
    }
}
