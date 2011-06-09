package no.statkart.skif.module;

import com.google.inject.Binder;
import com.google.inject.Key;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.MapConfiguration;
import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class ModuleStrategy implements Cloneable {
    protected Configuration configuration = new MapConfiguration();

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setProperties(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ModuleStrategy clone() {
        try {
            return (ModuleStrategy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ImplementationException(e);
        }
    }

    protected void requireBinding(Binder binder, Key<?> key) {
      binder.getProvider(key);
    }

    protected void requireBinding(Binder binder, Class<?> type) {
      binder.getProvider(type);
    }

}
