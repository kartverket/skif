package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import jakarta.annotation.Nullable;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.service.annotation.Call;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ClientCallServiceChainFactoryJEE<S> implements CallServiceChainFactory<S> {
    protected final TypeLiteral<S> type;
    protected final TerminatingProxyHandler<S> proxyHandler;
    private final Provider<List<ChainedProxyHandler<S>>> callProxyHandlerListProvider;

    @Inject
    public ClientCallServiceChainFactoryJEE(TypeLiteral<S> type, TerminatingProxyHandler<S> proxyHandler, @Call Provider<List<ChainedProxyHandler<S>>> callProxyHandlerListProvider) {
        this.type = type;
        this.proxyHandler = proxyHandler;
        this.callProxyHandlerListProvider = callProxyHandlerListProvider;
    }

    @Override
    public float getChainPosition() {
        return 0;  // Denne factory må være sist siden den avslutter kjeden.
    }

    @Override
    public ProxyHandler<S> createChain() {
        ProxyHandler<S> head = proxyHandler;
        final List<ChainedProxyHandler<S>> proxyHandlerList = callProxyHandlerListProvider.get();
        for (int i = proxyHandlerList.size() - 1; i >= 0; i--) {
            head = proxyHandlerList.get(i).setChained(head);
        }
        return head;
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        throw new NotImplementedException();
    }
}
