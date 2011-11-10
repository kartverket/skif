package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ModuleConfiguration {

    ModuleStrategyFactory getStrategyFactory();
    ServiceMode getServiceMode();
    Configuration getConfiguration();
}
