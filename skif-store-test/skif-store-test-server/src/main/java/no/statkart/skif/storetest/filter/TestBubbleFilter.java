package no.statkart.skif.storetest.filter;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreSessionReadListener;
import no.statkart.skif.store.StoreSessionWriteListener;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;
import no.statkart.skif.storetest.domain.standalone.FilteredBubbleId;

import java.util.Collection;

/**
 * Filtrerer objekter som er av typen FilteredBubble.
 * <p>
 * Filteret brukes også til å teste lasting av objekter hvor brukeren får PermissionDeniedException for
 * instanser det ikke er lov å laste. Bruker her teksten i feltet {@link FilteredBubble#getFilterText()} til
 * å simulere om brukren har lov til å se objektet eller ikke.
 *
 * @author Jan Holmen
 * @since 2.1
 */
public class TestBubbleFilter implements StoreSessionReadListener, StoreSessionWriteListener {
    private final static String replaced = "*******";

    private Collection<? extends BubbleObject> bubbleObjects;
    private int timesOnPreRegisterBubblesWasCalled;
    private int timesOnPostRegisterBubblesWasCalled;

    public Collection<? extends BubbleObject> getBubbleObjects() {
        return bubbleObjects;
    }

    public void setBubbleObjects(Collection<? extends BubbleObject> bubbleObjects) {
        this.bubbleObjects = bubbleObjects;
    }

    public int getTimesOnPreRegisterBubblesWasCalled() {
        return timesOnPreRegisterBubblesWasCalled;
    }

    public void setTimesOnPreRegisterBubblesWasCalled(int timesOnPreRegisterBubblesWasCalled) {
        this.timesOnPreRegisterBubblesWasCalled = timesOnPreRegisterBubblesWasCalled;
    }

    public int getTimesOnPostRegisterBubblesWasCalled() {
        return timesOnPostRegisterBubblesWasCalled;
    }

    public void setTimesOnPostRegisterBubblesWasCalled(int timesOnPostRegisterBubblesWasCalled) {
        this.timesOnPostRegisterBubblesWasCalled = timesOnPostRegisterBubblesWasCalled;
    }

    public void clear() {
        setBubbleObjects(null);
        setTimesOnPreRegisterBubblesWasCalled(0);
        setTimesOnPostRegisterBubblesWasCalled(0);
    }

    //******************************************
    //**                read                  **
    //******************************************

    @Override
    public <T extends BubbleObject> void onPreRegisterBubbles(Collection<? extends T> bubbleObjects) {
        this.bubbleObjects = bubbleObjects;
        timesOnPreRegisterBubblesWasCalled++;
    }

    @Override
    public void onPostRegisterBubbles() {
        timesOnPostRegisterBubblesWasCalled++;
    }

    @Override
    public <T extends BubbleObject> T onRegister(T bubbleObject) {
        if (bubbleObject instanceof FilteredBubble) {
            FilteredBubble fb = (FilteredBubble) bubbleObject;
            if (fb.isFilter()) {
                if (fb.getFilterText().contains("PermissionDenied")) {
                    throw new PermissionDeniedException(String.format("Ikke lov å laste objektet: %s", fb.getBubbleId().toString()));
                }
                FilteredBubble ro = new FilteredBubble(fb.getId(), fb.getText(), fb.isFilter(), replaced);
                return (T)ro;
            }
        }
        return bubbleObject;
    }

    //******************************************
    //**                write                 **
    //******************************************

    @Override
    public <T extends BubbleObject> T onInsert(T storeBubbleObject, T persistentBubbleObject) {
        if (storeBubbleObject instanceof FilteredBubble && persistentBubbleObject == null) {
            //nytt objekt, alltid?
            FilteredBubble clientObject = (FilteredBubble) storeBubbleObject;
            if(clientObject.isFilter()){
                throw new ImplementationException("Objekt kan ikke insertes med filtrerte felter!");
            }
        }
        return storeBubbleObject;
    }

    /**
     * Kunne ha oppdatert alle felter som ikke er filtrert, kaster i stede en exception for testing her.
     *
     * @param storeBubbleObject
     * @param persistentBubbleObject
     * @param <T>
     * @return
     */
    @Override
    public <T extends BubbleObject> T onUpdate(T storeBubbleObject, T persistentBubbleObject) {
        if (storeBubbleObject instanceof FilteredBubble && persistentBubbleObject instanceof FilteredBubble) {
            FilteredBubble clientObject = (FilteredBubble) storeBubbleObject;
            FilteredBubble serverObject = (FilteredBubble) persistentBubbleObject;
            if(serverObject.isFilter()){
                throw new ImplementationException("Objekt kan ikke insertes med filtrerte felter!");
            }
            OppdaterObjekt(clientObject, serverObject);
        } else {
            persistentBubbleObject = storeBubbleObject;
        }
        return persistentBubbleObject;
    }

    private void OppdaterObjekt(FilteredBubble clientObject, FilteredBubble serverObject) {
        if(serverObject.getId() == null){
            serverObject.setId(new FilteredBubbleId<FilteredBubble>(clientObject.getId().getValue()));
        }
        if (serverObject.getText() == null || !serverObject.getText().equals(clientObject.getText())) {
            serverObject.setText(clientObject.getText());
        }
        if (!serverObject.isFilter()){
            serverObject.setFilter(clientObject.isFilter());
        }
        if (!serverObject.isFilter()) {
            serverObject.setFilterText(clientObject.getFilterText());
        }
    }

    /**
     * Kaster exception hvis en prøver å slette objekter som er filtrert.
     *
     * @param storeBubbleObject
     * @param persistentBubbleObject
     * @param <T>
     * @return
     */
    @Override
    public <T extends BubbleObject> T onDelete(T storeBubbleObject, T persistentBubbleObject) {
        if (storeBubbleObject instanceof FilteredBubble && persistentBubbleObject instanceof FilteredBubble) {
            //ikke sikkert dette trengs ved delete.....
            FilteredBubble clientObject = (FilteredBubble) storeBubbleObject;
            FilteredBubble serverObject = (FilteredBubble) persistentBubbleObject;
            if(serverObject.isFilter()){
                throw new ImplementationException("Objekt kan ikke insertes med filtrerte felter!");
            }
            OppdaterObjekt(clientObject, serverObject);
        } else {
            persistentBubbleObject = storeBubbleObject;
        }
        return persistentBubbleObject;
    }

}
