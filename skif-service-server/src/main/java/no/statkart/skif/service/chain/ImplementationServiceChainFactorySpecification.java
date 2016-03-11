package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class ImplementationServiceChainFactorySpecification extends FactorySpecification<ImplementationServiceChainFactory> {
    private ArrayList<Class<? extends ChainedProxyHandler>> implementationCallChainProxyHandlerClassList;

    @SafeVarargs
    public ImplementationServiceChainFactorySpecification(Class<? extends ChainedProxyHandler>... implementationCallChainProxyHandlerClasses) {
        this(ImplementationServiceChainFactoryImpl.class, implementationCallChainProxyHandlerClasses);
    }

    @SafeVarargs
    public ImplementationServiceChainFactorySpecification(Class<? extends ImplementationServiceChainFactory> factoryClass, Class<? extends ChainedProxyHandler>... implementationCallChainProxyHandlerClasses) {
        super(factoryClass);
        implementationCallChainProxyHandlerClassList = new ArrayList<>(Arrays.asList(implementationCallChainProxyHandlerClasses));
    }

    public void appendImplementationServiceChainProxyHandler(Class<? extends ChainedProxyHandler> implementationServiceChainProxyHandler) {
        implementationCallChainProxyHandlerClassList.add(implementationServiceChainProxyHandler);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        final List<TypeLiteral<? extends ChainedProxyHandler<S>>> implementationCallChainProxyHandlerTypeList = new ArrayList<>(implementationCallChainProxyHandlerClassList.size());
        for (Class<? extends ChainedProxyHandler> chainedProxyHandlerImplClass : implementationCallChainProxyHandlerClassList) {
            TypeLiteral<? extends ChainedProxyHandler<S>> implementationCallChainProxyHandlerType = SkifUtil.typeLiteral(chainedProxyHandlerImplClass, service);
            binder.bind(implementationCallChainProxyHandlerType);
            implementationCallChainProxyHandlerTypeList.add(implementationCallChainProxyHandlerType);
        }
        TypeLiteral<List<ChainedProxyHandler<S>>> implementationCallChainProxyHandlerListType =
                (TypeLiteral<List<ChainedProxyHandler<S>>>) TypeLiteral.get(Types.listOf(Types.newParameterizedType(ChainedProxyHandler.class, service)));

        binder.bind(implementationCallChainProxyHandlerListType).annotatedWith(Implementation.class).toProvider(new Provider<List<ChainedProxyHandler<S>>>() {
            @Inject
            Injector injector;

            @Override
            public List<ChainedProxyHandler<S>> get() {
                List<ChainedProxyHandler<S>> list = new ArrayList<>(implementationCallChainProxyHandlerTypeList.size());
                for (TypeLiteral<? extends ChainedProxyHandler<S>> type : implementationCallChainProxyHandlerTypeList) {
                    final ChainedProxyHandler<S> proxyHandler = injector.getInstance(Key.get(type));
                    list.add(proxyHandler);
                }
                return list;
            }
        });
    }

    @Override
    public ImplementationServiceChainFactorySpecification clone() {
        ImplementationServiceChainFactorySpecification clone = (ImplementationServiceChainFactorySpecification) super.clone();
        clone.implementationCallChainProxyHandlerClassList = new ArrayList<>(implementationCallChainProxyHandlerClassList);
        return clone;
    }

}
