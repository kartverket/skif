package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.ejb.EJBResourceProxyHandler;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EJBServiceChainFactoryFactorySpecification som gir mulighet for å angi en ordnet liste av
 * {@code ChainedProxyHandler}-klasser som EJBServiceChainFactory'en skal sette opp.
 *
 * @author Henrik Fredholm
 * @see  EJBServiceChainFactoryImpl
 * @since 2.0
 */
public class EJBServiceChainFactorySpecification extends FactorySpecification<EJBServiceChainFactory> {
    private ArrayList<Class<? extends ChainedProxyHandler>> ejbCallChainProxyHandlerClassList;

    @SafeVarargs
    public EJBServiceChainFactorySpecification(Class<? extends ChainedProxyHandler>... ejbCallChainProxyHandlerClasses) {
        this(EJBServiceChainFactoryImpl.class, ejbCallChainProxyHandlerClasses);
    }

    @SafeVarargs
    public EJBServiceChainFactorySpecification(Class<? extends EJBServiceChainFactory> factoryClass, Class<? extends ChainedProxyHandler>... ejbResourceProxyHandlerImplentationClasses) {
        super(factoryClass);
        this.ejbCallChainProxyHandlerClassList = new ArrayList<>(Arrays.asList(ejbResourceProxyHandlerImplentationClasses));
    }

    public void appendEJBServiceChainProxyHandler(Class<? extends ChainedProxyHandler> ejbServiceChainProxyHandler) {
        ejbCallChainProxyHandlerClassList.add(ejbServiceChainProxyHandler);
    }


    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        final List<TypeLiteral<? extends ChainedProxyHandler<S>>> ejbCallChainProxyHandlerTypeList = new ArrayList<>(ejbCallChainProxyHandlerClassList.size());
        for (Class<? extends ChainedProxyHandler> chainedProxyHandlerImplClass : ejbCallChainProxyHandlerClassList) {
            TypeLiteral<? extends EJBResourceProxyHandler<S>>  ejbCallChainProxyHandlerType = SkifUtil.typeLiteral(chainedProxyHandlerImplClass, service);
            binder.bind(ejbCallChainProxyHandlerType);
            ejbCallChainProxyHandlerTypeList.add(ejbCallChainProxyHandlerType);
        }
        TypeLiteral<List<ChainedProxyHandler<S>>> ejbCallChainProxyHandlerListType =
                (TypeLiteral<List<ChainedProxyHandler<S>>>) TypeLiteral.get(Types.listOf(Types.newParameterizedType(ChainedProxyHandler.class, service)));

        binder.bind(ejbCallChainProxyHandlerListType).toProvider(new Provider<List<ChainedProxyHandler<S>>>() {
            @Inject
            Injector injector;

            @Override
            public List<ChainedProxyHandler<S>> get() {
                List<ChainedProxyHandler<S>> list = new ArrayList<>(ejbCallChainProxyHandlerTypeList.size());
                for (TypeLiteral<? extends ChainedProxyHandler<S>> type : ejbCallChainProxyHandlerTypeList) {
                    final ChainedProxyHandler<S> proxyHandler = injector.getInstance(Key.get(type));
                    list.add(proxyHandler);
                }
                return list;
            }
        });
    }

    /**
     * Oppretter en avhengighet fra modulen definert av {@code binder} til {@code type}.
     * Når injectoren opprettes så vil Guide rapportere en feil hvis {@code type} ikke
     * kan injectes.
     *
     * @since 2.0
     */
    protected void requireBinding(Binder binder, Class<?> type) {
        binder.getProvider(type);
    }

    @Override
    public EJBServiceChainFactorySpecification clone() {
        EJBServiceChainFactorySpecification clone = (EJBServiceChainFactorySpecification) super.clone();
        //noinspection unchecked
        clone.ejbCallChainProxyHandlerClassList = (ArrayList<Class<? extends ChainedProxyHandler>>) ejbCallChainProxyHandlerClassList.clone();
        return clone;
    }
}
