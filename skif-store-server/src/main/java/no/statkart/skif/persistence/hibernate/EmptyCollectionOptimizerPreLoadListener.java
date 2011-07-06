package no.statkart.skif.persistence.hibernate;

import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.util.Map;

/**
 * Hibernate listener that must be added to Hibernate in order for empty collection optimization to work. When this
 * listener is in use Hibernate will optimized away the loading of collections that are known to be empty.
 * 
 * @author Henrik Fredholm
 */
public class EmptyCollectionOptimizerPreLoadListener implements PreLoadEventListener {
   private Map<EntityPersister, EmptyCollectionsOptimizer> optimizers;

   public EmptyCollectionOptimizerPreLoadListener(Map<EntityPersister, EmptyCollectionsOptimizer> optimizers) {
      this.optimizers = optimizers;
   }

   public void onPreLoad(PreLoadEvent event) {
      EntityPersister persister = event.getPersister();
      EmptyCollectionsOptimizer optimizer = optimizers.get(persister);
      if (optimizer == null) {
         optimizer = EmptyCollectionsOptimizer.createOptimizer(persister);
         optimizers.put(persister, optimizer);
      }
      optimizer.initializeEmptyCollections(event, persister);
   }
}


