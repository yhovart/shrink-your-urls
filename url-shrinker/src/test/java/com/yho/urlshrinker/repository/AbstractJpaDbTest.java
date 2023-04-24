package com.yho.urlshrinker.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.yho.urlshrinker.config.JpaTestConfiguration;

// @ExtendWith(SpringExtension.class)
@DataJpaTest()
@ContextConfiguration(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
@TestPropertySource(properties = {
    "spring.profiles.active=jpa,random"
})
public class AbstractJpaDbTest {
    
}
