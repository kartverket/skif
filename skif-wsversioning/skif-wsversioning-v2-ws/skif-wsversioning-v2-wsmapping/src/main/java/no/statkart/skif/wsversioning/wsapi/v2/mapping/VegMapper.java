package no.statkart.skif.wsversioning.wsapi.v2.mapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.wsversioning.domain.Veg;

/**
 * Mapper som wrapper default type mapping pga. av versjonering av mindre endringer.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class VegMapper extends DefaultTypeMapper<no.statkart.skif.wsversioning.wsapi.v2.domain.Veg, Veg, WSVersioningMapping> {
    private final Provider<ServiceContext> serviceContextProvider;

    public VegMapper(Provider<ServiceContext> serviceContextProvider) {
        super(no.statkart.skif.wsversioning.wsapi.v2.domain.Veg.class, Veg.class, WSVersioningMapping.class);
        this.serviceContextProvider = serviceContextProvider;
    }

    @Override
    public no.statkart.skif.wsversioning.wsapi.v2.domain.Veg mapDomainObject(Veg source) {
        ServiceContext serviceContext = serviceContextProvider.get();

        no.statkart.skif.wsversioning.wsapi.v2.domain.Veg target = super.mapDomainObject(source);

        if (serviceContext.getSystemVersion().equals("2.0")) {
            // Dette feltet "fantes ikke i 2.0"
            target.setAlternativtNavn(null);
        }

        return target;
    }

    @Override
    public Veg mapWsapiObject(no.statkart.skif.wsversioning.wsapi.v2.domain.Veg source) {
        ServiceContext serviceContext = serviceContextProvider.get();

        // TODO: Mappingrammeverket mangler effektiv støtte for delvis mapping
        if (!serviceContext.getSystemVersion().equals("2.1")) {
            throw new MappingException("Støtter ikke mapping av annet enn versjon 2.1");
        }

        Veg target = super.mapWsapiObject(source);
        return target;
    }
}
