package no.statkart.skif.storetest;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.sql.DataSource;

@SpringBootTest
public class SkifSpringStoreTestServerApplicationTests extends AbstractTestNGSpringContextTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    @Qualifier("Old")
    DataSource dataSourceOld;

    @Test
    void contextLoads() {
        Assertions.assertNotSame(dataSource, dataSourceOld, "Current and Old datasource should differ");
    }

}
