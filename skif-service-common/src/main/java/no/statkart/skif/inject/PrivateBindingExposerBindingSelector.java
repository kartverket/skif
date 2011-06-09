package no.statkart.skif.inject;

import java.lang.annotation.Annotation;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface PrivateBindingExposerBindingSelector {
    PrivateBindingExposerBindingSelector usingBindingSelector(BindingKeySelector selector);

    void expose();

    void bindAndExposeAnnotatedWith(Annotation annotation);

}
