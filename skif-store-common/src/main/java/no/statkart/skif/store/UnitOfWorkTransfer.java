package no.statkart.skif.store;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * Data Transfer Object interface for transfering a unit of work with <code>BubbleRoot</code>s
 * over the network.
 *
 * TODO: Denne er tatt fra matrikkelen. Mener denne kan forbedres
 * @author Henrik Fredholm
 */
public class UnitOfWorkTransfer implements Serializable {
   static final long serialVersionUID = 1L;

   /**
    * All bubbles in this unit of work transfer, including removed objects
    */
   private final Map objects = new HashMap(250);
   /**
    * Ids for new objects in this unit of work transfer. Insert order is maintained.
    */
   private final Set newIds = new LinkedHashSet(150);
   /**
    * Ids for new objects in this unit of work transfer. Update order is maintained.
    */
   private final Set updatedIds = new LinkedHashSet(150);
   /**
    * Ids for removed objects in this unit of work transfer. Remove order is maintained.
    */
   private final Set removedIds = new LinkedHashSet(50);

   /**
    * Creates a new UnitOfWorkTransfer from an existing UnitOfWorkTransfer. The
    * new UnitOfWorkTransfer will reference the same objects as the incomming
    * UnitOfWorkTransfer.
    *
    * @param unitOfWorkTransfer
    */
   protected UnitOfWorkTransfer(UnitOfWorkTransfer unitOfWorkTransfer) {
      this(unitOfWorkTransfer.getObjects(), unitOfWorkTransfer.getRemovedIds(), unitOfWorkTransfer.getNewIds(), unitOfWorkTransfer.getUpdatedIds());
   }

   public UnitOfWorkTransfer(Map objects, List removedIds, List newIds, List updatedIds) {
      this(objects, new LinkedHashSet(removedIds), new LinkedHashSet(newIds), new LinkedHashSet(updatedIds));
   }

   /**
    * Creates a new UnitOfWorkTransfer from the specified arguments.
    *
    * @param objects    map of all bubbleObjects, including deleted bubbleObjects.
    * @param removedIds List of removed bubbleIds
    * @param newIds     List of inserted bubbleIds
    * @param updatedIds List of updated bubbleIds
    */

   public UnitOfWorkTransfer(Map objects, Set removedIds, Set newIds, Set updatedIds) {
      // Legg inn bare objekter som er referert
      for( Iterator<BubbleId> iterator = objects.keySet().iterator(); iterator.hasNext(); ) {
         BubbleId bubbleId = iterator.next();
         if( removedIds.contains(bubbleId) || newIds.contains(bubbleId) || updatedIds.contains(bubbleId) ) {
            this.objects.put(bubbleId, objects.get(bubbleId));
         }
      }

      for( Object o : removedIds ) {
         BubbleId id = (BubbleId) o;
         if( this.removedIds.contains(id) ) {
            throw new IllegalArgumentException("Denne fjernede id'en har allerede blitt lagt inn i denne UnitOfWork: " + id.toString());
         }
         this.removedIds.add(id);
      }
      for( Object o : newIds ) {
         BubbleId id = (BubbleId) o;
         if( this.newIds.contains(id) ) {
            throw new IllegalArgumentException("Denne nye id'en har allerede blitt lagt inn i denne UnitOfWork: " + id.toString());
         }
         this.newIds.add(id);
      }
      for( Object o : updatedIds ) {
         BubbleId id = (BubbleId) o;
         if( this.updatedIds.contains(id) ) {
            throw new IllegalArgumentException("Denne endrede id'en har allerede blitt lagt inn i denne UnitOfWork: " + id.toString());
         }
         this.updatedIds.add(id);
      }
      checkAllowedClasses();
   }

   /**
    * Get the ids for all new objects in this transfer. The ids will be sorted by the comparator returned by
    * {@link #getDependencyComparator()}, if not <code>null</code> is returned from that method.
    *
    * @return a list of <code>SpifBubbleId</code>s
    */
   final public List getNewIds() {
      Comparator comparator = getDependencyComparator();
      List newIdsList = new ArrayList(newIds);
      if( comparator != null ) Collections.sort(newIdsList, comparator);
      return Collections.unmodifiableList(newIdsList);
   }

   /**
    * Get the ids for all updated objects in this transfer.
    *
    * @return a list of <code>SpifBubbleId</code>s
    */
   final public List getUpdatedIds() {
      return Collections.unmodifiableList(new ArrayList(updatedIds));
   }

   /**
    * Get the ids for all removed objects in this transfer. The ids will be sorted in <it>reverse order</it> of the ordering
    * specified by the comparator returned by {@link #getDependencyComparator()}, if not <code>null</code> is returned
    * from that method.
    *
    * @return a list of <code>SpifBubbleId</code>s
    */
   final public List getRemovedIds() {
      Comparator comparator = getDependencyComparator();
      List removedIdsList = new ArrayList(removedIds);
      if( comparator != null ) {
         Collections.sort(removedIdsList, comparator);
         //Ordering of removals must opposite of inserts!
         Collections.reverse(removedIdsList);
      }
      return Collections.unmodifiableList(removedIdsList);
   }

   /**
    * Get a bubble contained in this model by its <code>id</code>.
    *
    * @param id the id of an object contained in this model
    * @return the object or null if the object is not contained
    */
   final public BubbleObject get(BubbleId id) {
      return (BubbleObject) objects.get(id);
   }

   /**
    * Get a set of bubbles contained in this model by their <code>ids</code>.
    *
    * @param ids a set of <code>SpifBubbleId</code>'s for objects contained in this model
    * @return the set of objects identified by the <code>ids</code>. If none of the <code>ids</code> is contained
    *         in this model, then an empty set is returned.
    */
   final public Set get(Collection ids) {
      Set bubbles = new HashSet();
      for( Iterator it = ids.iterator(); it.hasNext(); ) {
         BubbleId bubbleId = (BubbleId) it.next();
         bubbles.add(objects.get(bubbleId));
      }
      return bubbles;
   }

   /**
    * Gets a map with all objects contained in this transfer.
    *
    * @return Map of all objects in transfer
    */
   final public Map getObjects() {
      return Collections.unmodifiableMap(objects);
   }

   /**
    * Get an object contained in this transfer.
    *
    * @param bubbleId id for the object
    * @return a bubble object
    */
   final public BubbleObject getObject(BubbleId bubbleId) {
      return (BubbleObject) objects.get(bubbleId);
   }

   /**
    * This method can be overridden in sub classes to define a contraint on what kind of bubble objects
    * that a specific unit-of-work transfer can contain.
    *
    * @return a set of {@link Class} objects for sub types of {@link BubbleObject}
    */
   public Set getAllowedClasses() {
      return null;
   }

   /**
    * This method can be overridden i sub classes to define an ordering on the list of ids returned by {@link #getNewIds()}
    * and {@link #getRemovedIds()}. The ordering should be based on dependecies/relations between bubble objects.
    * <p/>
    * The comparator will define the ordering of the result from {@link #getNewIds()}.
    * The result from {@link #getRemovedIds()} will be the <it>reverse</it> of the ordering defined by the comparator.
    *
    * @return a comparator that defines the ordering between bubble objects/ids given the {@link BubbleId}.
    */
   //TODO: Fjern dependency comparator frå denne klassen, da UOW handterer dette fullt og heilt
   protected Comparator getDependencyComparator() {
      return null;
   }

   /**
    * Check that all objects referred to by new, updated and removed ids are of the types defined by {@link #getAllowedClasses()}.
    *
    * @throws RuntimeException if violations of allowed classes are found.
    */
   protected void checkAllowedClasses() {
      Set allowedClasses = getAllowedClasses();
      if( allowedClasses == null ) return;
      //todo? runtime exception?

      Set modifiedIds = new HashSet();
      // Bruker ikke getter her da sortering ikke er nødvendig.
      modifiedIds.addAll(newIds);
      modifiedIds.addAll(updatedIds);
      modifiedIds.addAll(removedIds);
      for( Iterator iterator = modifiedIds.iterator(); iterator.hasNext(); ) {
         BubbleId bubbleId = (BubbleId) iterator.next();
         if( !allowedClasses.contains(bubbleId.getType()) ) {
            //En modifisert klasse er ikke med i lista over tillatte klasser
            //Gå gjennom tillatte klasser og sjekk om modifisert klasses superklasse er der
            boolean funnet = false;
            for( Iterator iterator1 = allowedClasses.iterator(); iterator1.hasNext() && !funnet; ) {
               Class klasse = (Class) iterator1.next();
               if( klasse.isAssignableFrom(bubbleId.getType()) ) funnet = true;
            }
            if( !funnet )
               throw new RuntimeException("Object cannnot be modified with this type of transfer: " + bubbleId.getType());
         }
      }
   }

   public Set getNewAndUpdatedIds() {
      HashSet ids = new HashSet();
      ids.addAll(getNewIds());
      ids.addAll(getUpdatedIds());
      return ids;
   }

   public void checkObjectIntegrity() {
      Set<BubbleId> missingKeys = new HashSet<BubbleId>();
      for( Iterator it = newIds.iterator(); it.hasNext(); ) {
         BubbleId bubbleId = (BubbleId) it.next();
         if( !objects.containsKey(bubbleId) ) {
            missingKeys.add(bubbleId);
         }
      }
      for( Iterator it = updatedIds.iterator(); it.hasNext(); ) {
         BubbleId bubbleId = (BubbleId) it.next();
         if( !objects.containsKey(bubbleId) ) {
            missingKeys.add(bubbleId);
         }
      }
      if( !missingKeys.isEmpty() ){
         StringBuffer buffer = new StringBuffer();
         Iterator<BubbleId> iterator = missingKeys.iterator();
         buffer.append("Objects missing in unit of work-transfer: [ " + iterator.next() );
         for( ; iterator.hasNext(); ) {
            buffer.append(", ").append(iterator.next());
         }
         buffer.append(" ]");
         throw new RuntimeException(buffer.toString());
      }
   }
}
