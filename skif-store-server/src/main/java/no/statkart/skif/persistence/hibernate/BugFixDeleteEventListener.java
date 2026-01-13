package no.statkart.skif.persistence.hibernate;

import org.hibernate.event.internal.DefaultDeleteEventListener;
import org.hibernate.event.spi.DeleteContext;
import org.hibernate.event.spi.EventSource;
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
                                     EntityPersister persister, DeleteContext transientEntities) {
    super.deleteTransientEntity(session, entity, persister,
                                                       transientEntities == null ? DeleteContext.create() : transientEntities);
  }
}
