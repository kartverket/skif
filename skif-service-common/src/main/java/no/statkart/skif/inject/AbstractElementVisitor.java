package no.statkart.skif.inject;

import com.google.inject.Binding;
import com.google.inject.spi.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AbstractElementVisitor<V> implements ElementVisitor<V>{
    @Override
    public <T> V visit(Binding<T> binding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(InterceptorBinding binding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(ScopeBinding binding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(TypeConverterBinding binding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(InjectionRequest<?> request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(StaticInjectionRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> V visit(ProviderLookup<T> lookup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> V visit(MembersInjectorLookup<T> lookup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(Message message) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(PrivateElements elements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(TypeListenerBinding binding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(RequireExplicitBindingsOption option) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V visit(DisableCircularProxiesOption option) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
