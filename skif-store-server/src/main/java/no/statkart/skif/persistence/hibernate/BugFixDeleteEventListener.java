package no.statkart.skif.persistence.hibernate;

import org.hibernate.event.EventSource;
import org.hibernate.event.def.DefaultDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate bug: http://opensource.atlassian.com/projects/hibernate/browse/HHH-2146
 * @author Henrik Fredholm
 */
public class BugFixDeleteEventListener extends DefaultDeleteEventListener
{
protected void deleteTransientEntity(EventSource session, Object entity, boolean cascadeDeleteEnabled,
                                     EntityPersister persister, Set transientEntities) {
    super.deleteTransientEntity(session, entity, cascadeDeleteEnabled, persister,
                                                       transientEntities == null ? new HashSet() : transientEntities);
  }
}
