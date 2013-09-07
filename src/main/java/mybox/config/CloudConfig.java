package mybox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("cloud")
@Configuration
@PropertySource("classpath:cloud.properties")
public class CloudConfig {

}
