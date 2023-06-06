package no.statkart.skif.standalone.store.persistence.hibernate.type;

import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Testklasse for EnumKodeIdType. Sjekker at equals oppfører seg som forventet
 *
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
@Deprecated // TODO: skal flyttes
@Test
public class EnumKodeIdTypeTest {

    public void testEquals(){

        EnumKodeIdType type = new EnumKodeIdType();
        Assert.assertTrue(type.equals(null, null));
        Assert.assertFalse(type.equals(null, 12L));
        Assert.assertFalse(type.equals(AEnumKodeId.KodeAId, null));
        Assert.assertTrue(type.equals(AEnumKodeId.KodeAId, AEnumKodeId.KodeAId));
        Assert.assertFalse(type.equals(AEnumKodeId.KodeBId, AEnumKodeId.KodeAId));

    }
}
