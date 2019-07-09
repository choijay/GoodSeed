package goodseed.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

/**
 * The class ExceptionManager
 * @author jay
 *
 */
@Aspect
public class ExceptionManager {

	private static final Log LOG = LogFactory.getLog(ExceptionManager.class);

	@AfterThrowing(pointcut = "(execution(* *..service.*Service.*(..)))", throwing = "ex")
	public void exceptionHandler(JoinPoint joinPoint, Throwable ex) throws Throwable {

		ex.printStackTrace();

		// 에러 출력
		if(LOG.isErrorEnabled()) {
			LOG.error("exceptionHandler called : " + ex);
		}

		/**
		 * UserHandleException 일 경우에는 throw, RuntimeException 일 경우에만 SystemException 으로 wrapping
		 */
		if(ex instanceof UserHandleException) {
			throw ex;
		} else {

			SystemException systemException = new SystemException(ex);
			/**
			 * ENI SMS 전송 모듈 호출
			 */
//			String excptNo = ENIManager.eniModule(joinPoint, systemException);
//			systemException.setExceptionNo(excptNo);

			throw systemException;
		}
	}
}
