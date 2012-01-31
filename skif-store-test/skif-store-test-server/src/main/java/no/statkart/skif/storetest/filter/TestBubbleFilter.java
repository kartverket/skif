package no.statkart.skif.storetest.filter;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreSessionReadListener;
import no.statkart.skif.store.StoreSessionWriteListener;
import no.statkart.skif.storetest.domain.demo.FilteredBubble;
import no.statkart.skif.storetest.domain.demo.FilteredBubbleId;

/**
 * Filtrerer objekter som er av typen FilteredBubble.
 * <p/>
 * Hvis egenskap
 *
 * @author Jan Holmen
 * @since 2.1
 */
public class TestBubbleFilter implements StoreSessionReadListener, StoreSessionWriteListener {
    private final static String replaced = "*******";

    //******************************************
    //**                read                  **
    //******************************************
    @Override
    public <T extends BubbleObject> T onRegister(T bubbleObject) {
        if (bubbleObject instanceof FilteredBubble) {
            FilteredBubble fb = (FilteredBubble) bubbleObject;
            if (fb.isFilter()) {
                fb.setFilterText(replaced);
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
     *
     * Kunne ha oppdatert alle felter som ikke er filtrert, kaster i stede en exception for tesing her.
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
