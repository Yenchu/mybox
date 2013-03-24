package mybox.config;

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


@Configuration
@Import({ServiceConfig.class})
@ComponentScan("mybox.task")
@EnableAsync
@EnableAspectJAutoProxy
@PropertySource("classpath:app.properties")
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
    public ProfilingAspect profilingAspect() {
        return new ProfilingAspect();
    }
}
