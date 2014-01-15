package no.statkart.skif.service;

import no.statkart.skif.mapper.Mapping;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ServiceContextMapper<W> {
    W  createWSServiceContextFromDomainServiceContext(Mapping map);
    void setDomainServiceContextFromWSServiceContext(Mapping map, W apiContext);
}
