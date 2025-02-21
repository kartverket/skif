package no.statkart.skif.service;

import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;

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
            throw new ImplementationException("Locale parameter må være satt på context!");
        }

        String[] strings = locale.split("_");

        if(strings.length < 2){
            throw new ImplementationException("Locale parameter in context must have two or three parts, each made up of two letter, separated by _. First part is from ISO-639, second part from ISO-3166, while third part is variant");
        }

        if(!strings[0].toLowerCase().equals(strings[0])) {
            throw new ImplementationException("First part of Locale in context must follow ISO-639 and consist of two lower case letters");
        }

        if(!strings[1].toUpperCase().equals(strings[1])) {
            throw new ImplementationException("Second part of Locale in context must follow ISO-3166 and consist of two upper case letters");
        }

        //Språkvariant strings[2] kan inneholde både store og små bokstaver og må mappes hvis den er oppgitt.

        if(strings.length==2){
            return new Locale(strings[0], strings[1]);
        } else if( strings.length == 3){
            return new Locale(strings[0], strings[1], strings[2]);
        }
        throw new ImplementationException("Locale is not of the form 'no_NO' or 'no_NO_NY', can not map");
    }

}
