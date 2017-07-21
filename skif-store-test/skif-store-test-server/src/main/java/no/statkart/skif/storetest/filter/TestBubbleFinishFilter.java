package no.statkart.skif.storetest.filter;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.StoreSessionFinishListener;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;
import no.statkart.skif.storetest.domain.standalone.FilteredBubbleId;

import java.util.LinkedHashSet;

/**
 * Test finisFilter.
 * @since 2.1
 * @author Jan Holmen
 */
public class TestBubbleFinishFilter implements StoreSessionFinishListener {
    @Override
    public void onFinish(StoreServer storeServer) {
        LinkedHashSet<BubbleId<?>> insertedIds = storeServer.getInsertedIds();

        for(BubbleId<?> id : insertedIds){
            if(id instanceof FilteredBubbleId){
                BubbleObject bubbleObject = storeServer.get(id);
                if(bubbleObject instanceof FilteredBubble){
                    FilteredBubble fb = (FilteredBubble) bubbleObject;
                    if("Finish 101".equals(fb.getText())){
                        fb.setFilterText("overskrevet");
                    }else if("Fail 101".equals(fb.getText())){
                        throw new ImplementationException("Kastet feil pga. innhold i tekst");
                    }
                }
            }
        }
    }
}
