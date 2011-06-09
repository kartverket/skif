package no.statkart.skif.service.ejb;

import no.statkart.skif.service.proxy.TerminatingProxyHandler;

/**
 * En ProxyHandler for service av type {@code S} som gjør kall til servicens EJB implementasjon. ProxyHandleren
 * håndterer EJB spesifikke exceptions slik at det blir transparent om kallet går via EJB eller direkte.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class EJBCallProxyHandler<S> extends TerminatingProxyHandler<S> {
}
