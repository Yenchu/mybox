package mybox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("dev")
@Configuration
@PropertySource("classpath:dev.properties")
public class DevConfig {

}
