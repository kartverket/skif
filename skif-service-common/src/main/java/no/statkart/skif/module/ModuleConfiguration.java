package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface ModuleConfiguration {

    ModuleStrategyFactory getStrategyFactory();
    ServiceMode getServiceMode();
    Configuration getConfiguration();
}
