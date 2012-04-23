package no.statkart.skif.skiftest.domain;

import no.statkart.skif.skiftest.domain.array1.G;
import no.statkart.skif.skiftest.domain.array1.H;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Roar Ingebrigtsen
 * @since 3.0
 */
@Test
public class ArrayMapperTest {

    public void testMappingViaDefaultTypeMapper(){

        ArrayTestMapper mapper = new ArrayTestMapper();

        G source = new G();
        source.setClasses(new Class[]{java.lang.String.class, java.lang.Integer.class});
        source.setInts(new int[]{0, 1, 2, 3});
        source.setStrings(new String[]{"s1", "s2", "s3"});
        source.setHs(new H[]{new H(1), new H(2)});
        no.statkart.skif.skiftest.domain.array2.G target = mapper.getMapping().d2w(source);

        Assert.assertEquals(target.getClasses(), source.getClasses());
        Assert.assertEquals(target.getInts(), source.getInts());
        Assert.assertEquals(target.getStrings(), source.getStrings());
        for(int i = 0; i < source.getHs().length; i++){
            H sourceH = source.getHs()[i];
            no.statkart.skif.skiftest.domain.array2.H targetH = target.getHs()[i];
            Assert.assertEquals(targetH.getNr(), sourceH.getNr());
        }

        G target2 = mapper.getMapping().w2d(target);

        Assert.assertEquals(target2.getClasses(), source.getClasses());
        Assert.assertEquals(target2.getInts(), source.getInts());
        Assert.assertEquals(target2.getStrings(), source.getStrings());
        for(int i = 0; i < source.getHs().length; i++){
            H sourceH = source.getHs()[i];
            H targetH = target2.getHs()[i];
            Assert.assertEquals(targetH.getNr(), sourceH.getNr());
        }

    }

}


