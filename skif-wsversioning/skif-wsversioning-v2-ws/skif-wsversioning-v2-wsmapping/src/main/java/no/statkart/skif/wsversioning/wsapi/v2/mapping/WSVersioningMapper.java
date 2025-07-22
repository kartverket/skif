package no.statkart.skif.wsversioning.wsapi.v2.mapping;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.CollectionMapperFactory;
import no.statkart.skif.mapper.DefaultTypeMapperFactory;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;
import no.statkart.skif.mapper.MappingResolver;
import no.statkart.skif.mapper.SnapshotVersionTypeMapper;
import no.statkart.skif.mapper.TimestampTypeMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.wsversioning.domain.VegId;
import no.statkart.skif.wsversioning.wsapi.v2.domain.Timestamp;

/**
 * Mapper for WSVersioning-prosjektets V2-API.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningMapper extends AbstractMapper<WSVersioningMapping> {
    @Inject
    public WSVersioningMapper(Provider<ServiceContext> serviceContextProvider) {
        super(WSVersioningMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.wsversioning.wsapi.v2.domain", "no.statkart.skif.wsversioning.domain");
        setMappingResolver(mappingResolver);


        // Klasser hvor objekter skal mappes til seg selv
        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMapperFactory(new CollectionMapperFactory());

        addMapperFactory(new DefaultTypeMapperFactory(serviceContextProvider));


        addMapper(new SnapshotVersionTypeMapper<>(Timestamp.class));
        addMapper(new TimestampTypeMapper<>(Timestamp.class));

        // Alle Id-er
        addMapper(new WSVersioningBubbleIdTypeMapper<>(no.statkart.skif.wsversioning.wsapi.v2.domain.VegId.class, VegId.class));

    }
}
