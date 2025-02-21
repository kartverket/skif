package no.statkart.skif.store.module.common;

import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;

/**
 * Denne klassen definerer en {@code RemoteServiceModuleStrategy} som bestemmer hvilken {@code SnapshotVersion} som
 * skal gjelde for kallet ved å hente ut {@code SnapshotVersion} fra en forutbestemt parameter i kallet eller
 * fra {@code SnapshotVersionContext} dersom parametrene ikke anvendes.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@SuppressWarnings("UnusedDeclaration") // Reflection
public abstract class RemoteServiceModuleStrategyWithServiceContextSVMapper extends RemoteServiceModuleStrategy {
}
