package no.statkart.skif.inject;

import com.google.inject.Module;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface PrivateBindingExposerModuleSelector {
    PrivateBindingExposerBindingSelector selectBindingsIn(Module m);
}
