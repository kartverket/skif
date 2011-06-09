package no.statkart.skif.inject;

import com.google.inject.Module;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface PrivateBindingExposerModuleSelector {
    PrivateBindingExposerBindingSelector selectBindingsIn(Module m);
}
