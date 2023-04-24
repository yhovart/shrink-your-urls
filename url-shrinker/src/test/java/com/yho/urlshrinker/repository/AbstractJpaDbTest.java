package com.yho.urlshrinker.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.yho.urlshrinker.config.JpaTestConfiguration;

@ExtendWith(SpringExtension.class)
@DataJpaTest()
@ContextConfiguration(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
@TestPropertySource(properties = {
    "spring.profiles.active=jpa,random"
})
public class AbstractJpaDbTest {
    
}
