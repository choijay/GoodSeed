/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.logging;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * The class ServiceLogger<br>
 * <br>
 * Service 호출에 대한 로깅을 하기 위해 만든 클래스<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 *
 */
@Aspect
public class ServiceLogger {

	private static final Log LOG = LogFactory.getLog(ServiceLogger.class);

	@Pointcut("execution(* *..service.*Service.*(..))")
	public void serviceMethods() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## ServiceLogger serviceMethods");
		}
	}

	/**
	 * pointcut과 advice 조건에 의해 invoke되는 메서드<br>
	 * <br>
	 * @param joinPoint the join point
	 * @throws Throwable 
	 * @return 타겟 객체의 원본 메소드 실행
	 */
	@Around("serviceMethods()")
	public Object serviceMethodProfile(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		String methodName = joinPoint.getSignature().toShortString();
		if(LOG.isInfoEnabled()) {
			stopWatch = new StopWatch();
			String target = joinPoint.getTarget().toString();
			methodName = joinPoint.getSignature().toShortString();
			String classPath = target.substring(0, target.lastIndexOf('@'));
			/* logging service method call. */
			StringBuilder logMessage = new StringBuilder(classPath);
			logMessage.append(".").append(methodName).append("() called.");
			LOG.info(logMessage);

			/* Database session logging. */
			//			LoggingDatabaseSession session = new LoggingDatabaseSession();
			//			session.setModuleId(classPath);
			//			session.setActionId(methodName);

			stopWatch.start();
		}
		/* method invoke */
		Object result = joinPoint.proceed();

		if(LOG.isInfoEnabled()) {
			stopWatch.stop();

			/* logging elapsed time */
			StringBuilder elapsedTime = new StringBuilder("Service method [");
			elapsedTime.append(methodName).append("] elapsed time : ");
			elapsedTime.append(stopWatch.getTime()).append(" ms.");
			LOG.info(elapsedTime);
		}
		return result;
	}

}
