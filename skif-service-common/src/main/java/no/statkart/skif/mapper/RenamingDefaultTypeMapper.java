package no.statkart.skif.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * Subklasse av DefaultTypeMapper som skal håndtere at feltnavn på en side har et annet navn på den andre.
 *
 * @author Steinar Hansen
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
    protected Field overrideTargetField(Field sourceField, Field targetField) {
        // Søk etter sourceField i renamedFields
        Field retVal = null;
        if (renamedFields != null) {
            for (Map.Entry<String, String> entry : renamedFields.entrySet()) {
                String wsapiFieldName = entry.getKey();
                String domainFieldName = entry.getValue();

                if (sourceField.getName().equals(domainFieldName)) {
                    retVal = getFieldWithInheritedFields(targetField.getDeclaringClass(), wsapiFieldName);
                }else if(sourceField.getName().equals(wsapiFieldName)){
                    retVal = getFieldWithInheritedFields(targetField.getDeclaringClass(), domainFieldName);
                }
            }
        }

        return retVal;
    }
}