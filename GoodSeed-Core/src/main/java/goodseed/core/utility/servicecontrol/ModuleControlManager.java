/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.servicecontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import goodseed.core.common.model.Parameters;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;
import goodseed.core.exception.SystemException;
import goodseed.core.exception.UserHandleException;

/**
 * The class BlockServiceManager<br>
 * <br>
 * 성능저하 원인 서비스 일시적 차단 수행 클래스<br>
 * <br>
 * @author jay
 * @version 1.0
 * @since 2. 29.
 *
 */
@Aspect
public class ModuleControlManager {

	private static final Log LOG = LogFactory.getLog(ModuleControlManager.class);

	@Pointcut("execution(* *..module.*Module.*(..))")
	public void serviceMethods() {
		// Do nothing because of X and Y.
	}

	/**
	 * Service method profiling.<br>
	 * <br>
	 * @param joinPoint the join point
	 * @throws Throwable 
	 */
	@Around("serviceMethods()")
	public Object serviceMethodProfile(final ProceedingJoinPoint joinPoint) throws Throwable {

		Object[] obj = joinPoint.getArgs();

		//호출 인자 타입이 첫번째 파라미터가 Parameters 이면서 두번째 타입은 propagation 일 경우에만 통과
		if(obj.length == 0 || !(obj[0] instanceof Parameters) || obj.length != 2) {
			return joinPoint.proceed();
		}

		int propagation = (Integer)obj[1];

		PlatformTransactionManager transactionManager =
				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean("transactionManager");
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(propagation);
		return transactionTemplate.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				Object result = null;
				try {
					result = joinPoint.proceed();
				} catch(UserHandleException ue) {
					status.setRollbackOnly();
					LOG.error("UserHandleException", ue);
					throw ue;
				} catch(SystemException re) {
					status.setRollbackOnly();
					LOG.error("SystemException", re);
					throw re;
				} catch(Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status.setRollbackOnly();
					LOG.error("Throwable", e);
					throw new SystemException(e);
				}

				return result;
			}
		});
	}
}
