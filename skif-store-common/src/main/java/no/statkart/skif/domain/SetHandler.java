package no.statkart.skif.domain;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link Set#equals(Object)} kaller {@link Object#equals(Object)} på elementene. Vi ønsker vanligvis ikke dette, men
 * heller bruke {@link EqualsByFields} for å sammenligne.
 * <p/>
 * Denne oppfyller kontrakten for {@link Set#equals(Object)}, som sier at settene må ha samme lengde, alle elementene i
 * det ene settet må ha en maken i det andre settet og alle elementene i det andre settet må ha en maken i det første.
 */
public class SetHandler implements EqualityHandler<Set<?>> {
    @Override
    public boolean checkEquals(Set<?> o1, @Nullable Object o2, EqualsByFields comparator) {
        if (o1 == o2) return true;
        if (!(o2 instanceof Set)) return false;

        Set<?> s2 = (Set<?>) o2;

        if (o1.size() != s2.size()) return false;

        Map<?, ?> m2 = s2.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));

        for (Object e1 : o1) {
            // Hvis e1 og e2 er EqualsByFields, så er de også equals slik HashMap ser det
            Object e2 = m2.get(e1);

            // Gjør ikke null-sjekk her. Dette skal påfølgende kode håndtere selv.
            if (!comparator.isEqualByFields(e1, e2)) {
                return false;
            }
        }

        return true;
    }
}
