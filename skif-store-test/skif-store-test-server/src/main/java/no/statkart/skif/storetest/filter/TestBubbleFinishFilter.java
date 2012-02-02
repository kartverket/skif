package no.statkart.skif.storetest.filter;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.StoreSessionFinishListener;
import no.statkart.skif.storetest.domain.demo.FilteredBubble;
import no.statkart.skif.storetest.domain.demo.FilteredBubbleId;

import java.util.LinkedHashSet;

/**
 * Created by IntelliJ IDEA.
 * User: holjan
 * Date: 01.02.12
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class TestBubbleFinishFilter implements StoreSessionFinishListener {
    @Override
    public void onFinish(StoreServer storeServer) {
        LinkedHashSet<BubbleId<?>> insertedIds = storeServer.getInsertedIds();
//        LinkedHashSet<BubbleId<?>> deletedIds = storeServer.getDeletedIds();
//        LinkedHashSet<BubbleId<?>> updatedIds = storeServer.getUpdatedIds();
//        LinkedHashSet<BubbleId<?>> lockedIds = storeServer.getLockedIds();  '

        for(BubbleId id : insertedIds){
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
