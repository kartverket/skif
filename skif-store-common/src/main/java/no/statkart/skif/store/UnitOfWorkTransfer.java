package no.statkart.skif.store;


import com.google.common.collect.Sets;
import no.statkart.skif.exception.ImplementationException;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Transferobjekt som brukes for overføre endringer gjort på klient til server. For inserts og
 * updates overføres selve objektet, mens for deletes overføres kun id.
 * <p/>
 * Objektet har en valideringsmetode, {@link #checkAllowedClasses()} for å sjekke at transferobjektet kun inneholder gyldige klasser. Som default
 * tillates alle bobleklasser. Metoden kan gjøres mer restriktiv i subklasser. Metoden kalles på serveren i forbindelse med
 * registrering av transferen på tjeneren.
 *
 * @author Henrik Fredholm
 */
public class UnitOfWorkTransfer implements Serializable {
    static final long serialVersionUID = 1L;

    /**
     * Inserted objekter.
     */
    private  List<BubbleObject> insertedObjects;
    /**
     * Updated objekter
     */
    private  List<BubbleObject> updatedObjects;
    /**
     * Id'er for deleted objekter
     */
    private  List<BubbleObject> deletedObjects;

    @Deprecated // WS-mapping
    public UnitOfWorkTransfer() {
    }

    public UnitOfWorkTransfer(List<? extends BubbleObject> insertedObjects, List<? extends BubbleObject> updatedObjects, List<? extends BubbleObject> deletedObjects) {
        this.insertedObjects = (List<BubbleObject>) insertedObjects;
        this.updatedObjects = (List<BubbleObject>) updatedObjects;
        this.deletedObjects = (List<BubbleObject>) deletedObjects;
    }

    public boolean isShared() {
        boolean isShared = false;
        if (!insertedObjects.isEmpty()) {
            isShared = insertedObjects.get(0).store()!=null;
        } else if (!updatedObjects.isEmpty()) {
            isShared = updatedObjects.get(0).store()!=null;
        } else if (!deletedObjects.isEmpty()) {
            isShared = deletedObjects.get(0).store()!=null;
        }
        return isShared;
    }

    public List<BubbleObject> getInsertedObjects() {
        return insertedObjects;
    }

    public List<BubbleObject> getUpdatedObjects() {
        return updatedObjects;
    }

    public List<BubbleObject> getDeletedObjects() {
        return deletedObjects;
    }

    /**
     * Kontroller at transferen kun inneholder gyldige klasser. I utgangspunktet tillates alle objekter hvis klasse eller superklasse ligger i
     * {@link #getAllowedClasses()},
     *
     * @throws ImplementationException hvis objekt ikke er av gyldig klasse
     */
    public void checkAllowedClasses() throws ImplementationException {
        final Set<Class<? extends BubbleObject>> allowedClasses = getAllowedClasses();
        for (BubbleObject object : insertedObjects) {
            checkAllowedClass(object.getClass(), allowedClasses);
            checkIdClass(object.getClass(), object.getId());
        }

        for (BubbleObject object : updatedObjects) {
            checkAllowedClass(object.getClass(), allowedClasses);
            checkIdClass(object.getClass(), object.getId());
        }

        for (BubbleObject object : deletedObjects) {
            checkAllowedClass(object.getClass(), allowedClasses);
            checkIdClass(object.getClass(), object.getId());
        }
    }

    protected void checkIdClass(Class<? extends BubbleObject> bubbleClass, BubbleId<?> id) {
        if (id==null) {
            throw new ImplementationException("Id can not be null for class: " +  bubbleClass);
        }
        if (id.getType()!=bubbleClass) {
            throw new ImplementationException("Instance of class " + bubbleClass.getName() + " has id of non-matching type " + id);
        }
    }

    protected void checkAllowedClass(Class<? extends BubbleObject> bubbleClass, Set<Class<? extends BubbleObject>> allowedClasses) throws ImplementationException {
        for (Class<?extends BubbleObject> allowedClass : allowedClasses) {
            if (allowedClass.isAssignableFrom(bubbleClass)) {
                return;
            }
        }
        throw new ImplementationException("Objects of this class can not be modified with this type of transfer: " + bubbleClass.getName());

    }

    public Set<Class<? extends BubbleObject>> getAllowedClasses() {
        Set<Class<? extends BubbleObject>> allowedClasses =  Sets.newHashSet();
        allowedClasses.add(BubbleObject.class);
        return allowedClasses;
    }

    @Deprecated // WS-mapping
    public void setInsertedObjects(List<BubbleObject> insertedObjects) {
        this.insertedObjects = insertedObjects;
    }

    @Deprecated // WS-mapping
    public void setUpdatedObjects(List<BubbleObject> updatedObjects) {
        this.updatedObjects = updatedObjects;
    }

    @Deprecated // WS-mapping
    public void setDeletedObjects(List<BubbleObject> deletedObjects) {
        this.deletedObjects = deletedObjects;
    }
}
