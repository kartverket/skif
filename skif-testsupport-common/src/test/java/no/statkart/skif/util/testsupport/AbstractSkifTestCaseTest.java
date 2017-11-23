package no.statkart.skif.util.testsupport;

import no.statkart.skif.module.ModuleBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class AbstractSkifTestCaseTest {

    private TestWithMembers testCase;

    @BeforeMethod
    public void setUp() throws Exception {
        testCase = new TestWithMembers();
        AbstractSkifTestCase.resetFieldsToNull(testCase);
    }

    @Test
    public void modifyNonStaticNonFinal() {
        assertThat(testCase.foo).isNull();
    }

    @Test
    public void modifyNonStaticFinal() throws IllegalAccessException {
        assertThat(testCase.fooFinal).isNull();
    }

    @Test
    public void staticFinalIsNotModified() {
        assertThat(TestWithMembers.fooStaticFinal).isNotNull();
    }

    @Test
    public void staticIsModified() {
        assertThat(TestWithMembers.fooStatic).isNull();
    }

    private static class TestWithMembers extends AbstractSkifTestCase {
        private String foo = "bar";
        private final Integer fooFinal = 3;
        private static final Integer fooStaticFinal = 4;
        private static Integer fooStatic = 5;

        @Override
        protected ModuleBuilder createReusableModuleBuilder() {
            return null;
        }
    }
}