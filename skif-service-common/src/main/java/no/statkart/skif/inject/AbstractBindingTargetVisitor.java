package no.statkart.skif.inject;

import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AbstractBindingTargetVisitor<T, V> implements BindingTargetVisitor<T, V>{
    @Override
    public V visit(InstanceBinding<? extends T> instanceBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ProviderInstanceBinding<? extends T> providerInstanceBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ProviderKeyBinding<? extends T> providerKeyBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(LinkedKeyBinding<? extends T> linkedKeyBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ExposedBinding<? extends T> exposedBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(UntargettedBinding<? extends T> untargettedBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ConstructorBinding<? extends T> constructorBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ConvertedConstantBinding<? extends T> convertedConstantBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ProviderBinding<? extends T> providerBinding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
