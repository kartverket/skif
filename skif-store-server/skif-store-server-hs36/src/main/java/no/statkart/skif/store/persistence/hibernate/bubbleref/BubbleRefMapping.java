package no.statkart.skif.store.persistence.hibernate.bubbleref;

import org.hibernate.engine.Mapping;
import org.hibernate.mapping.PersistentClass;

/**
 * @author Henrik Fredholm
 */
public interface BubbleRefMapping extends Mapping {

     PersistentClass getClassMapping(String entityName);

}
