package no.statkart.skif.service;

import com.google.inject.Provider;
import no.statkart.skif.exception.ValidationException;

import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @author Jan Holmen
 * @since 2.0
 */
public abstract class AbstractServiceContextMapper<C> implements ServiceContextMapper<C> {
    protected final Provider<ServiceContext> serviceContextProvider;

    public AbstractServiceContextMapper(Provider<ServiceContext> serviceContextProvider) {
        this.serviceContextProvider = serviceContextProvider;
    }

    protected Locale localeFromString(String locale) {

        if(locale == null) {
            throw new ValidationException("Locale parameter må være satt på context!");
        }

        String[] strings = locale.split("_");

        if(strings.length < 2){
            throw new ValidationException("Locale parameter på context skal ha to eller tre deler, hver på to bokstaver, separert med _ Første del skal følge ISO-639, andre del skal følge ISO-3166, tredje ledd er språkvariant");
        }

        if(strings[0].toLowerCase() != strings[0]) {
            throw new ValidationException("Første del av Locale på context skal følge standarden ISO-639 og være to små bokstaver", null);
        }

        if(strings[1].toUpperCase() != strings[1]) {
            throw new ValidationException("Andre del av Locale på context skal følge standarden ISO-3166 og være to store bokstaver", null);
        }

        //Språkvariant strings[2] kan inneholde både store og små bokstaver og må mappes hvis den er oppgitt.

        if(strings.length==2){
            return new Locale(strings[0], strings[1]);
        } else if( strings.length == 3){
            return new Locale(strings[0], strings[1], strings[2]);
        }
        throw new ValidationException("Locale er ikke på formen 'no_NO' eller 'no_NO_NY', ingen mapping gjort", null);
    }

}
