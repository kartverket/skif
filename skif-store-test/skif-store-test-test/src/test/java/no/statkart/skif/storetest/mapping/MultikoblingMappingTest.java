package no.statkart.skif.storetest.mapping;

import no.statkart.skif.store.multikobling.KoblingFactory;
import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest.domain.multikobling.MultirefererendeKobling;
import no.statkart.skif.storetest.wsapi.domain.basetyper.StringList;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Tester at mappinglogikken klarer å mappe ut disse {@link Set}-ene som {@link Multikobling#get(Object)} returnerer.
 * Disse kan ikke mappes inn, da brukes vanlige {@code Set}, så det trenger ikke testes.
 */
public class MultikoblingMappingTest {
    private final StoreTestMapper mapper = new StoreTestMapper(() -> null);
    private final StoreTestMapping mapping = mapper.getMapping();

    @Test
    public void mapBubbleWithMultikobling() throws NoSuchMethodException {
        Multikobling<String, String, MultirefererendeKobling> multikobling = Multikobling.create(((KoblingFactory<String, String, MultirefererendeKobling>) MultirefererendeKobling::new));
        multikobling.put("A", "1");
        Set<String> kobling = getKobling(multikobling, "A");

        Method getKobling = MultikoblingMappingTest.class.getDeclaredMethod("getKobling", Multikobling.class, String.class);

        StringList mapped = (StringList) mapping.d2w(kobling, getKobling.getGenericReturnType(), StringList.class);
        Assertions.assertThat(mapped.getItem()).containsExactly("1");
    }

    /**
     * Dette er en metode så testen kan hente typeinformasjon ut fra signaturen.
     */
    private Set<String> getKobling(Multikobling<String, String, ?> multikobling, String rolle) {
        return multikobling.get(rolle);
    }
}
