package mybox.config;

import java.util.Properties;
import java.util.concurrent.Executor;

import mybox.aspect.ProfilingAspect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

@Configuration
@EnableAspectJAutoProxy
@EnableAsync
@PropertySource("classpath:app.properties")
@Import({ServiceConfig.class})
@ComponentScan("mybox.task")
public class AppConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(50);
        executor.initialize();
        return executor;
	}

	@Bean
	public SystemProp systemProp() {
		return new SystemProp();
	}
	
	@Bean
    public ProfilingAspect profilingAspect() {
        return new ProfilingAspect();
    }
	
	@Bean
	public VelocityEngineFactoryBean velocityEngineFactoryBean() {
		VelocityEngineFactoryBean velocityEngine = new VelocityEngineFactoryBean();
		//velocityEngine.setResourceLoaderPath("/WEB-INF/velocity/");
		Properties prop = new Properties();
		/*prop.setProperty("resource.loader", "webapp");
		prop.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.WebappResourceLoader");
		prop.setProperty("webapp.resource.loader.path", "/WEB-INF/velocity/");*/
		prop.setProperty("resource.loader", "class");
		prop.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setVelocityProperties(prop);
		return velocityEngine;
	}
	
	/*@Bean
	@Scope("prototype")
    public RestClient restClient() {
        return new RestClientImpl();
    }*/
}
