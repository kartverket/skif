package no.statkart.skif.wsversioning.wsapi.v2.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultTypeMapper;
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
        dtm.addPackageMapping("no.statkart.skif.wsversioning.wsapi.v2.domain", "no.statkart.skif.wsversioning.domain");
        setDefaultMapper(dtm);


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);
        useIdentityMapping(Boolean.class);

        addMapper(new SnapshotVersionTypeMapper());

        // Alle Id-er
        addMapper(new WSVersioningBubbleIdTypeMapper<no.statkart.skif.wsversioning.wsapi.v2.domain.VegId, VegId>(no.statkart.skif.wsversioning.wsapi.v2.domain.VegId.class, VegId.class));
    }
}
