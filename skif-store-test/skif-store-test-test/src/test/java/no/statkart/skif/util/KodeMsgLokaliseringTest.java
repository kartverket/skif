package no.statkart.skif.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase5;
import org.junit.Assert;

import org.testng.annotations.Test;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: holjan
 * Date: 29.11.11
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
@Test
public class KodeMsgLokaliseringTest {

    public KodeMsgLokaliseringTest() {
        super();
    }

    class MsgModule extends AbstractModule{
        @Override
        protected void configure() {
            bind(KodeMsg.class).to(DemoKodeMsg.class);
        }
    }




    public void testLokaliseringInject(){
        Injector incjector = Guice.createInjector(new MsgModule());
        KodeMsg kodeMsg = incjector.getInstance(KodeMsg.class);
        String key = "AEnumKode.B";

        String kodeString = kodeMsg.getString(key,new Locale("no","NO"));
        Assert.assertNotNull(kodeString);
        Assert.assertNotSame(key,kodeString);

        String kodeString2 = kodeMsg.getString(key, new Locale("no","NO","NY"));
        Assert.assertNotNull(kodeString2);
        Assert.assertNotSame(key, kodeString2);
        Assert.assertNotSame(kodeString,kodeString2);
        Assert.assertTrue(kodeString2.contains("(nynorsk)"));
    }
}
