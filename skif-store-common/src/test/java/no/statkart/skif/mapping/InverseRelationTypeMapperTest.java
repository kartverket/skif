package no.statkart.skif.mapping;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.InverseRelation;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link InverseRelationTypeMapper}
 */
@Test
public class InverseRelationTypeMapperTest {
    public static class DummyOptionalValue {
        private boolean materialised;
        private String cachedValue;

        public boolean isMaterialised() {
            return materialised;
        }

        public void setMaterialised(boolean materialised) {
            this.materialised = materialised;
        }

        public String getCachedValue() {
            return cachedValue;
        }

        public void setCachedValue(String cachedValue) {
            this.cachedValue = cachedValue;
        }
    }

    public interface TestMapping extends Mapping {
        DummyOptionalValue d2w(InverseRelation<String> source);
        InverseRelation<String> w2d(DummyOptionalValue source);
    }

    private static class TestMapper extends AbstractMapper<TestMapping> {
        public TestMapper() {
            super(TestMapping.class);

            addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

            addMapper(new InverseRelationTypeMapper<DummyOptionalValue, InverseRelation<String>>(DummyOptionalValue.class, SkifUtil.typeLiteral(InverseRelation.class, String.class)));
        }
    }

    private final TestMapping mapping;

    public InverseRelationTypeMapperTest() {
        mapping = new TestMapper().getMapping();
    }

    public void testMapping() {
        InverseRelation<String> inverseRelation = InverseRelation.create(null, null);

        inverseRelation.setMaterialised(true);
        inverseRelation.setCached("Hello");

        DummyOptionalValue optionalValue = mapping.d2w(inverseRelation);

        Assert.assertEquals(optionalValue.isMaterialised(), true);
        Assert.assertEquals(optionalValue.getCachedValue(), "Hello");

        InverseRelation<String> inverseRelationMapped = mapping.w2d(optionalValue);
        Assert.assertEquals(inverseRelationMapped.isMaterialised(), true);
        Assert.assertEquals(inverseRelationMapped.get(), "Hello");
    }
}
