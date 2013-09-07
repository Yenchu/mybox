package mybox.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigurableApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger log = LoggerFactory.getLogger(ConfigurableApplicationContextInitializer.class);
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		String appServerName = System.getProperty("OPENSHIFT_APP_UUID");
		if (StringUtils.isNotBlank(appServerName)) {
			log.info("App {} is deployed in cloud.", appServerName);
			applicationContext.getEnvironment().setActiveProfiles("cloud");
		} else {
			log.info("App is deployed in dev.");
			applicationContext.getEnvironment().setActiveProfiles("dev");
		}
	}

}
