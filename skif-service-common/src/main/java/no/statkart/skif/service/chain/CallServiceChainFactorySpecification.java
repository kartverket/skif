package no.statkart.skif.service.chain;

import com.google.inject.*;
import com.google.inject.util.Types;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.annotation.Call;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class CallServiceChainFactorySpecification extends FactorySpecification<CallServiceChainFactory> {
    private ArrayList<Class<? extends ChainedProxyHandler>> callChainProxyHandlerClassList;

    public CallServiceChainFactorySpecification(Class<? extends CallServiceChainFactory> factoryClass, Class<? extends ChainedProxyHandler>... callChainProxyHandlerClasses) {
        super(factoryClass);
        callChainProxyHandlerClassList = new ArrayList<Class<? extends ChainedProxyHandler>>(Arrays.asList(callChainProxyHandlerClasses));
    }

    public void appendCallServiceChainProxyHandler(Class<? extends ChainedProxyHandler> callServiceChainProxyHandler) {
        callChainProxyHandlerClassList.add(callServiceChainProxyHandler);
    }

    public ArrayList<Class<? extends ChainedProxyHandler>> getCallServiceChainProxyHandlers() {
        return callChainProxyHandlerClassList;
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        final List<TypeLiteral<? extends ChainedProxyHandler<S>>> callChainProxyHandlerTypeList = new ArrayList<TypeLiteral<? extends ChainedProxyHandler<S>>>(callChainProxyHandlerClassList.size());
        for (Class<? extends ChainedProxyHandler> chainedProxyHandlerImplClass : callChainProxyHandlerClassList) {
            TypeLiteral<? extends ChainedProxyHandler<S>> callChainProxyHandlerType = SkifUtil.typeLiteral(chainedProxyHandlerImplClass, service);
            binder.bind(callChainProxyHandlerType);
            callChainProxyHandlerTypeList.add(callChainProxyHandlerType);
        }
        TypeLiteral<List<ChainedProxyHandler<S>>> callChainProxyHandlerListType =
                (TypeLiteral<List<ChainedProxyHandler<S>>>) TypeLiteral.get(Types.listOf(Types.newParameterizedType(ChainedProxyHandler.class, service)));

        binder.bind(callChainProxyHandlerListType).annotatedWith(Call.class).toProvider(new Provider<List<ChainedProxyHandler<S>>>() {
            @Inject
            Injector injector;

            @Override
            public List<ChainedProxyHandler<S>> get() {
                List<ChainedProxyHandler<S>> list = new ArrayList<ChainedProxyHandler<S>>(callChainProxyHandlerTypeList.size());
                for (TypeLiteral<? extends ChainedProxyHandler<S>> type : callChainProxyHandlerTypeList) {
                    final ChainedProxyHandler<S> proxyHandler = injector.getInstance(Key.get(type));
                    list.add(proxyHandler);
                }
                return list;
            }
        });
    }

    @Override
    public CallServiceChainFactorySpecification clone() {
        CallServiceChainFactorySpecification clone = (CallServiceChainFactorySpecification) super.clone();
        clone.callChainProxyHandlerClassList = new ArrayList<Class<? extends ChainedProxyHandler>>(callChainProxyHandlerClassList);
        return clone;
    }

}
