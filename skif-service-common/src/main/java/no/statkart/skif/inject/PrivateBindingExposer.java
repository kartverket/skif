package no.statkart.skif.inject;

import com.google.inject.*;
import com.google.inject.spi.*;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class PrivateBindingExposer implements PrivateBindingExposerModuleSelector, PrivateBindingExposerBindingSelector {
    private final PrivateBinder privateBinder;
    private Module module;
    private Annotation annotation;
    private BindingKeySelector bindingSelector = new BindingKeySelector();


    public static PrivateBindingExposer use(PrivateBinder binder) {
        return new PrivateBindingExposer(binder);
    }

    private PrivateBindingExposer(PrivateBinder privateBinder) {
        this.privateBinder = privateBinder;
    }

    @Override
    public PrivateBindingExposerBindingSelector selectBindingsIn(Module module) {
        this.module = module;
        return this;
    }

    @Override
    public PrivateBindingExposerBindingSelector usingBindingSelector(BindingKeySelector selector) {
        this.bindingSelector = selector;
        return this;
    }

    @Override
    public void expose() {
        exposeBindings();
    }

    @Override
    public void bindAndExposeAnnotatedWith(Annotation annotation) {
        this.annotation = annotation;
        exposeBindings();
    }

    private void exposeBindings() {
        List<Element> elements = Elements.getElements(module);
        for (Element element : elements) {
            element.acceptVisitor(new AbstractElementVisitor<Void>() {
                @Override
                public <T> Void visit(Binding<T> binding) {
                    binding.acceptTargetVisitor(bindingSelector);
                    return null;
                }
            });
        }
        for (Key<Object> key : bindingSelector.getKeys()) {
            if (annotation == null) {
                privateBinder.expose(key.getTypeLiteral());
            } else {
                privateBinder.bind(key.getTypeLiteral()).annotatedWith(annotation).to(key.getTypeLiteral());
                privateBinder.expose(key.getTypeLiteral()).annotatedWith(annotation);
            }
        }
    }
}

