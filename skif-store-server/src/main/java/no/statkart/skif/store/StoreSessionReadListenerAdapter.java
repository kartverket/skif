package no.statkart.skif.store;

import java.util.Collection;

public class StoreSessionReadListenerAdapter implements StoreSessionReadListener{
    @Override
    public <T extends BubbleObject> T onRegister(T bubbleObject) {
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject> void onPreRegisterBubbles(Collection<? extends T> bubbleObjects) {
    }

    @Override
    public void onPostRegisterBubbles() {
    }
}
