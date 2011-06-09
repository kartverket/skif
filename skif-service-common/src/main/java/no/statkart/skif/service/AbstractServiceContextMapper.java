package no.statkart.skif.service;

import com.google.inject.Provider;
import no.statkart.skif.exception.ValidationException;

import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class AbstractServiceContextMapper<C> implements ServiceContextMapper<C> {
    protected final Provider<ServiceContext> serviceContextProvider;

    public AbstractServiceContextMapper(Provider<ServiceContext> serviceContextProvider) {
        this.serviceContextProvider = serviceContextProvider;
    }

    protected Locale localeFromString(String locale) {

        if(locale == null) {
            throw new ValidationException("", "", "Locale parameter må være satt på context!");
        }

        String[] strings = locale.split("_");

        if(strings.length != 2){
            throw new ValidationException("", "", "Locale parameter på context skal ha to deler, hver på to bokstaver, separert med _ Første del skal følge ISO-639 og andre del skal følge ISO-3166");
        }

        if(strings[0].toLowerCase() != strings[0]) {
            throw new ValidationException("", "", "Første del av Locale på context skal følge standarden ISO-639 og være to små bokstaver", null);
        }

        if(strings[1].toUpperCase() != strings[1]) {
            throw new ValidationException("", "", "Andre del av Locale på context skal følge standarden ISO-3166 og være to store bokstaver", null);
        }

        return new Locale(strings[0], strings[1]);
    }

}
