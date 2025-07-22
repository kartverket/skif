package no.statkart.skif.inject;

import com.google.inject.Key;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BindingKeySelector extends AbstractBindingTargetVisitor<Object, Void> {
    private List<Key<Object>> keys = new ArrayList<Key<Object>>();

    public List<Key<Object>> getKeys() {
        return keys;
    }

    @Override
    public Void visit(InstanceBinding<? extends Object> instanceBinding) {
        if (instanceBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>)instanceBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(UntargettedBinding<? extends Object> untargettedBinding) {
        if (untargettedBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) untargettedBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(LinkedKeyBinding<? extends Object> linkedKeyBinding) {
        if (linkedKeyBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) linkedKeyBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(ProviderInstanceBinding<? extends Object> providerInstanceBinding) {
        if (providerInstanceBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) providerInstanceBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(ProviderKeyBinding<? extends Object> providerKeyBinding) {
        if (providerKeyBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) providerKeyBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(ExposedBinding<? extends Object> exposedBinding) {
        if (exposedBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) exposedBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(ConstructorBinding<? extends Object> constructorBinding) {
        if (constructorBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) constructorBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(ConvertedConstantBinding<? extends Object> convertedConstantBinding) {
        if (convertedConstantBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) convertedConstantBinding.getKey());
        }
        return null;
    }

    @Override
    public Void visit(ProviderBinding<? extends Object> providerBinding) {
        if (providerBinding.getKey().getAnnotation() == null) {
            keys.add((Key<Object>) providerBinding.getKey());
        }
        return null;
    }
}
