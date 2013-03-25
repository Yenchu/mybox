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

	@Pointcut("execution(* mybox.rest.RestClient+.p*(..))")
    private void http3P() {}
	
	@Pointcut("execution(* mybox.rest.RestClient+.g*(..))")
    private void httpGet() {}
	
	@Pointcut("execution(* mybox.rest.RestClient+.d*(..))")
    private void httpDelete() {}
	
	@Around("http3P() || httpGet() || httpDelete()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		Object retVal = pjp.proceed();
		long endTime = System.currentTimeMillis() - start;
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		String methodName = signature.getMethod().getName();
		log.info("Method '{}' spent {} ms.", methodName, endTime);
		return retVal;
	}
}
