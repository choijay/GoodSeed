/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.servicecontrol;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;

/**
 * The class ServiceControlManager<br>
 * <br>
 * 성능저하 원인 서비스 일시적 차단 수행 클래스<br>
 * <br>
 * @author jay
 * @version 1.0
 * @since 2. 29.
 *
 */
@Aspect
public class ServiceControlManager {

	/**
	 * Service method profiling.<br>
	 * <br>
	 * @param joinPoint the join point
	 * @throws Throwable 
	 */
	@Before("execution(* *..service.*Service.*(..))")
	public void blockCallService(JoinPoint joinPoint) throws Throwable {

		String opMode = Config.getString("operation.mode").toUpperCase();

		if("WKR".equals(opMode) || "DEV".equals(opMode)) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for(StackTraceElement s : stackTrace) {
				String className = s.getClassName().substring(s.getClassName().lastIndexOf(".") + 1, s.getClassName().length());
				if(className.endsWith("Service")) {
					// 서비스에서 콜되었으므로 차단함
					throw new UserHandleException(Config.getString("customVariable.msgComErr041"));
				} else if(className.endsWith("Module")) {
					throw new UserHandleException(Config.getString("customVariable.msgComErr042"));
				}
			}
		}
	}

}
