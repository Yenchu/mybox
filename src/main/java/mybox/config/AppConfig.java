package mybox.config;

import java.util.concurrent.Executor;

import mybox.aspect.ProfilingAspect;
import mybox.rest.RestClient;
import mybox.rest.RestClientImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
	@Scope("prototype")
    public RestClient restClient() {
        return new RestClientImpl();
    }
}
