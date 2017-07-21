package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.wsversioning.domain.VegId;

/**
 * Mapper for WSVersioning-prosjektets V2-API.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningMapper extends AbstractMapper<WSVersioningMapping> {
    public WSVersioningMapper() {
        super(WSVersioningMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.wsversioning.wsapi.v1.domain", "no.statkart.skif.wsversioning.domain");
        /*dtm.overrideClassMappings(ImmutableMap.of(
                no.statkart.skif.wsversioning.wsapi.v1.domain.Gate.class, Veg.class,
                Veg.class, no.statkart.skif.wsversioning.wsapi.v1.domain.Gate.class
        ));*/
        setMappingResolver(mappingResolver);

        // Klasser hvor objekter skal mappes til seg selv
        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMapperFactory(new CollectionMapperFactory());

        addMapperFactory(new DefaultTypeMapperFactory());

        addMapper(new SnapshotVersionTypeMapper());

        // Alle Id-er
        addMapper(new WSVersioningBubbleIdTypeMapper<>(no.statkart.skif.wsversioning.wsapi.v1.domain.GateId.class, VegId.class));

        // Ting som må mappes manuelt pga. API-endringer
        addMapper(new GateMapper());
    }
}
