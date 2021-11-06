package no.statkart.skif.storetest.service.storetest1;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.springtestutils.MockSkifServiceContextTestExecutionListener;
import no.statkart.skif.springtestutils.WithMockSkifServiceContext;
import no.statkart.skif.storetest.config.StoreTestServerInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@SpringBootTest
@WithMockSkifServiceContext(injectorClass = StoreTestServerInjector.class)
@TestExecutionListeners( // Denne annotasjon kreves i tillegg når man bruker TestNG i stedet for JUnit
        value = {WithSecurityContextTestExecutionListener.class, MockSkifServiceContextTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class StoreTest1ServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    StoreTest1Service storeTest1Service;

    @Test
    @WithMockUser(username="testUser",roles={"Innsyn"})
    public void testStoreTest1Service() {
        storeTest1Service.clear();
        assertEquals(storeTest1Service.put("key1", "value1"), null);
        assertEquals(storeTest1Service.get("key1"), "value1");
        try {
            storeTest1Service.putThatFails("key1", "value2");
            fail("Forventet exception");
        } catch (Throwable t) {
            assertThat(t).describedAs("forventet exception").isInstanceOf(ImplementationException.class);
        }
        assertEquals(storeTest1Service.get("key1"), "value1", "Forrige metode skulle ikke ha endret 'key1'");
        assertEquals(storeTest1Service.remove("key1"), "value1");
        assertEquals(storeTest1Service.get("key1"), null);
    }
}
