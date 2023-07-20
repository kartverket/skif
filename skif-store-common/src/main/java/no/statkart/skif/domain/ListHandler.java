package no.statkart.skif.domain;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * {@link List#equals(Object)} kaller {@link Object#equals(Object)} på elementene. Vi ønsker vanligvis ikke dette, men
 * heller bruker {@link EqualsByFields} for å sammenligne.
 * <p/>
 * Denne oppfyller kontrakten for {@link List#equals(Object)}, som sier at listene må ha samme lengde og elementene må
 * parvis være like i listensrekkefølge.
 */
public class ListHandler implements EqualityHandler<List<?>> {
    @Override
    public boolean checkEquals(List<?> o1, @Nullable Object o2, EqualsByFields comparator) {
        if (o1 == o2) return true;
        if (!(o2 instanceof List)) return false;

        List<?> l2 = (List<?>) o2;

        if (o1.size() != l2.size())
            return false; // Dette kan være dyrt for enkelte lister, men vi pleier å bruke lister som vet svaret på forhånd.

        Iterator<?> i1 = o1.iterator();
        Iterator<?> i2 = l2.iterator();

        while (i1.hasNext() && i2.hasNext()) {
            Object e1 = i1.next();
            Object e2 = i2.next();

            if (!comparator.isEqualByFields(e1, e2)) return false;
        }

        return !(i1.hasNext() || i2.hasNext()); // Skal alltid være true så lenge størrelsene sammenlignes over.
    }
}
