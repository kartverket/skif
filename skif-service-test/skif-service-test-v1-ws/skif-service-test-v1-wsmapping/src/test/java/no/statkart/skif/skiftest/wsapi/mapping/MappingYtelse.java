package no.statkart.skif.skiftest.wsapi.mapping;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.domain.M;

import java.util.Map;
import java.util.Set;

/**
 * Lite program som dytter masse ting gjennom mapping slik at ytelsen kan måles.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class MappingYtelse {
    public static void main(String[] args) {
        SkifTestMapper<?> mapper = new SkifTestMapper();
        SkifTestMapping mapping = mapper.getMapping();

        Object[] objects = new Object[] {
                1,
                true,
                new A("10"),
                new B("10"),
                createM()
        };

        for (int i = 0; i < 100000; ++i) {
            for (Object object : objects) {
                mapping.d2w(object, Object.class);
            }
        }
    }

    private static M createM() {
        Set<A> as1 = Sets.newHashSet(new A("X"), new A("Y"));
        Set<A> as2 = Sets.newHashSet(new A("I"), new A("J"));
        Map<String, Set<A>> map = Maps.newHashMap();
        map.put("1", as1);
        map.put("2", as2);

        M m = new M();
        m.setMapOfAs(map);
        return m;
    }
}
