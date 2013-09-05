package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import com.google.common.collect.ImmutableMap;
import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.wsversioning.domain.Veg;
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

        // DefaultTypeMapper. Brukes for objekter som har samme properties i domenene
        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.wsversioning.wsapi.v1.domain", "no.statkart.skif.wsversioning.domain");
        /*dtm.overrideClassMappings(ImmutableMap.of(
                no.statkart.skif.wsversioning.wsapi.v1.domain.Gate.class, Veg.class,
                Veg.class, no.statkart.skif.wsversioning.wsapi.v1.domain.Gate.class
        ));*/
        setDefaultMapper(dtm);


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);
        useIdentityMapping(Boolean.class);

        addMapper(new SnapshotVersionTypeMapper());

        // Alle Id-er
        addMapper(new WSVersioningBubbleIdTypeMapper<no.statkart.skif.wsversioning.wsapi.v1.domain.GateId, VegId>(no.statkart.skif.wsversioning.wsapi.v1.domain.GateId.class, VegId.class));

        // Ting som må mappes manuelt pga. API-endringer
        addMapper(new GateMapper());
    }
}
