package no.statkart.skif.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;


/**
 * Subklasse av DefaultTypeMapper som skal håndtere at feltnavn på en side har et annet navn på den andre.
 *
 * @author Steinar Hansen
 * @author Tor Egil R. Strand
 */
public class RenamingDefaultTypeMapper<WsapiT, DomainT> extends DefaultTypeMapper<WsapiT, DomainT> {
    private Logger logger = LoggerFactory.getLogger(RenamingDefaultTypeMapper.class);
    // Formatet på dette feltet er "wsapiFieldName:domainFieldName"
    protected Map<String, String> renamedFields;


    public RenamingDefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    public RenamingDefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Map<String, String> renamedFields) {
        super(wsapiClass, domainClass);
        this.renamedFields = renamedFields;
    }

    @Override
    protected Method overrideSetter(Method sourceGetter, Method targetSetter, Class targetClass) {
        // Søk etter sourceField i renamedFields
        Method retVal = null;
        if (renamedFields != null) {
            for (Map.Entry<String, String> entry : renamedFields.entrySet()) {
                String wsapiFieldName = entry.getKey();
                String domainFieldName = entry.getValue();

                if (sourceGetter.getName().equals(accessorNameFromPropertyName("get", domainFieldName))) {
                    String setterName = accessorNameFromPropertyName("set", wsapiFieldName);
                    retVal = findSetter(targetClass, setterName);
                } else if (sourceGetter.getName().equals(accessorNameFromPropertyName("get", wsapiFieldName))) {
                    String setterName = accessorNameFromPropertyName("set", domainFieldName);
                    retVal = findSetter(targetClass, setterName);
                }
            }
        }

        return retVal;
    }

    private String accessorNameFromPropertyName(String getOrSet, String propertyName) {
        return getOrSet + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    private Method findSetter(Class targetClass, String setterName) {
        for (Method method : targetClass.getMethods()) {
            if (!method.isBridge() && method.getName().equals(setterName) && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        return null;
    }
}