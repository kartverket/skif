package no.statkart.skif.store.persistence.hibernate;

import org.hibernate.boot.Metadata;

public interface MetadataInterceptor {

    void apply(Metadata metadata);

}