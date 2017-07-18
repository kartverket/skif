package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.MapConfiguration;
import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class DefaultModuleConfiguration implements ModuleConfiguration {
    private ModuleStrategyFactory strategyFactory;
    private Configuration configuration;

    public DefaultModuleConfiguration() {
        this(null, null);
    }

    public DefaultModuleConfiguration(Configuration configuration) {
        this(configuration, null);
    }
    public DefaultModuleConfiguration(ModuleStrategyFactory factory) {
        this(null, factory);
    }

    public DefaultModuleConfiguration(Configuration configuration, ModuleStrategyFactory strategyFactory) {
        this.configuration = (configuration==null) ? new MapConfiguration() : configuration;
        this.strategyFactory = (strategyFactory==null) ? createStrategyFactory() : strategyFactory;
    }

    public DefaultModuleConfiguration setStrategyFactory(ModuleStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
        return this;
    }

    @Override
    public ModuleStrategyFactory getStrategyFactory() {
        return strategyFactory;
    }


    @Override
    public ServiceMode getServiceMode() {
        String serviceModeStr = configuration.getString(SkifConfigConstants.SERVICE_MODE);
        if (serviceModeStr != null) {
            return ServiceMode.valueOf(serviceModeStr);
        } else {
            return configuration.getBoolean(SkifConfigConstants.SINGLE_VM, false) ? ServiceMode.SINGLE_VM : ServiceMode.JEE;
        }

    }

    public DefaultModuleConfiguration setServiceMode(ServiceMode serviceMode) {
        configuration.setProperty(SkifConfigConstants.SERVICE_MODE, serviceMode.name());
        return this;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    protected ModuleStrategyFactory createStrategyFactory() {
        final String classname = configuration.getString(SkifConfigConstants.MODULE_STRATEGY_FACTORY_CLASS);
        if (classname==null) return null;

        Class<? extends ModuleStrategyFactory> strategyFactoryClass = SkifUtil.classForName(classname);
        try {
            //noinspection UnnecessaryLocalVariable
            final ModuleStrategyFactory moduleStrategyFactory = strategyFactoryClass.newInstance();
            return moduleStrategyFactory;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ImplementationException("Could not instantiate ModuleStrategyFactory class: " + strategyFactoryClass.getName(), e);
        }
    }

}
