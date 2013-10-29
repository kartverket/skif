package no.statkart.skif.store.endringslogg;

import no.statkart.skif.store.BubbleId;

import java.util.HashMap;
import java.util.Map;

/**
 * Deklarativ konfigurasjon for {@link AbstractEndringManager}
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 28
 */
public abstract class AbstractEndringManagerConfiguration<E extends AbstractEndring> {

    private Map<Class<? extends BubbleId>, Class<? extends E>> endringsklasser = new HashMap<Class<? extends BubbleId>, Class<? extends E>>();



    public Map<Class<? extends BubbleId>, Class<? extends E>> getEndringsklasser() {
        return endringsklasser;
    }


    // helper methods... ->
    protected EndringConfiguration declare(Class<? extends E> endringClass) {
        return new EndringConfiguration(endringClass);
    }


    public class EndringConfiguration {
        private Class<? extends E> endringClass;
        private Class<? extends BubbleId> bubbleIdClass;

        private EndringConfiguration(Class<? extends E> endringClass) {
            this.endringClass = endringClass;
        }

        public EndringConfiguration forBubbleId(Class<? extends BubbleId> bubbleIdClass) {
            this.bubbleIdClass = bubbleIdClass;
            addEndringsklasse();
            return this;
        }

        private void addEndringsklasse() {
            endringsklasser.put(bubbleIdClass, endringClass);
        }

    }

}
