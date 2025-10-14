package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.WithOneToManyBubbleRef;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;

public class OneToManyBubbleRefListener implements FlushEntityEventListener, PostLoadEventListener {
    @Override
    public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
        if (event.getEntity() instanceof WithOneToManyBubbleRef) {
            WithOneToManyBubbleRef entity = (WithOneToManyBubbleRef) event.getEntity();
            entity.preFlush((clazz, id) -> event.getSession().load(clazz, id, LockMode.NONE));
        }
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        if (event.getEntity() instanceof WithOneToManyBubbleRef) {
            WithOneToManyBubbleRef entity = (WithOneToManyBubbleRef) event.getEntity();
            entity.postLoad();
        }
    }
}
