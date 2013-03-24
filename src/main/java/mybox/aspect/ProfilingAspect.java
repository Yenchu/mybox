package mybox.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ProfilingAspect {

	private static final Logger log = LoggerFactory.getLogger(ProfilingAspect.class);

	@Pointcut("execution(* com.cloudena.larkspur.service.RestService+.p*(..))")
    private void httpPostAndPut() {}
	
	@Pointcut("execution(* com.cloudena.larkspur.service.RestService+.g*(..))")
    private void httpGet() {}
	
	@Around("httpPostAndPut() || httpGet()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		Object retVal = pjp.proceed();
		long endTime = System.currentTimeMillis() - start;
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		String methodName = signature.getMethod().getName();
		log.info("Method '{}' execution time {} ms.", methodName, endTime);
		return retVal;
	}
}
