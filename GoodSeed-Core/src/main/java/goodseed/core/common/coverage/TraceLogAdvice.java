package goodseed.core.common.coverage;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import goodseed.core.common.coverage.model.TraceLogParameters;
import goodseed.core.utility.config.Config;

/**
 * The class TraceTest<br>
 * <br>
 * TODO 설명을 상세히 입력하세요.<br>
 * <br>
 * <br>
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 4.0
 * @since  8. 17.
 *
 */
@Aspect
@Intercepts({@org.apache.ibatis.plugin.Signature(type = StatementHandler.class, method = "query", args = {Statement.class,
		ResultHandler.class})})
public class TraceLogAdvice implements Interceptor {

	private static final Log LOG = LogFactory.getLog(TraceLogAdvice.class);

	//	@Autowired
	//	private TraceLogManagerImpl traceLogManagerImpl;

	/**
	 * Target Class(Controller class) PointCut 지정 메서드<br>
	 * <br>
	 * @param N/A
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	//	@Pointcut(" execution(* cj.common.education.controller.EducationGridController.*(..)) ")
	@Pointcut(" execution(* cj.common.template.controller.*Controller.*(..)) OR execution(* cj.common.syscommon.controller.*Controller.*(..)) ")
	public void controllerPointCut() {

	}

	/**
	 * Target Class(Controller class) PointCut 지정 메서드 호출 시점에 클래스 및 메서드 정보 추출 클래스<br>
	 * 추가로 UI의 trace 정보도 추출<br>
	 * <br>
	 * @param joinPoint the join point
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@Before("controllerPointCut()")
	public void controllerAdvice(JoinPoint joinPoint) {

		String opMode = Config.getString("operation.mode").toUpperCase();

		if(!TraceLogInfoConstant.OP_MODE_PRD.equals(opMode)) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("## Trace Log - Controller(Before)");
			}

			String target = joinPoint.getTarget().toString();
			Signature signature = joinPoint.getSignature();
			String controllerPkgNm = target.substring(0, target.lastIndexOf('@'));
			String controllerFullPath = new StringBuilder(controllerPkgNm).append(".").append(signature.getName()).toString();
			String controllerMethodNm = signature.getName();

			if(LOG.isDebugEnabled()) {
				LOG.debug("## Trace Log - Controller Full Path : " + controllerFullPath);
			}

			/**
			 * 1) ThreadLocal 객체 생성
			 * 1) TranId(거래아이디) 생성 : UUID 이용.
			 * 2) UI Request Param 정보를 이용하여 UI Trace 항목 추출
			 * 3) UI 최종 호출자와 Controller Mapping을 통한 caller, callee 설정
			 */
			TraceLogInfo traceLogInfo = new TraceLogInfo();

			TraceLogParameters traceLogParameters = new TraceLogParameters();
			traceLogParameters.setTRAN_ID(traceLogInfo.getUuid());
			traceLogParameters.setCALLEE_HASH_VAL(String.valueOf(controllerFullPath.hashCode()));
			traceLogParameters.setCALLER_HASH_VAL(TraceLogInfoConstant.ROOT_HASH_VAL);
			traceLogParameters.setCALLEE_PKG_NM(controllerPkgNm);
			traceLogParameters.setCALLEE_METHOD_NM(controllerMethodNm);
			traceLogParameters.setCALLER_PKG_NM("ROOT");
			traceLogParameters.setCALLER_METHOD_NM("ROOT");
			traceLogParameters.setCALLEE_TYPE(TraceLogInfoConstant.CONTROLLER_TYPE_VALUE);

			List<TraceLogParameters> traceList = new ArrayList<TraceLogParameters>();
			traceList.add(traceLogParameters);
			traceLogInfo.setTraceLogInfoList(traceList);

			TraceLogThreadLocalManager.setTraceLogInfo(traceLogInfo);
		}
	}

	/**
	 * Target Class(Controller class) 정상 실행 후 trace 정보 DB write<br>
	 * <br>
	 * @param joinPoint the join point
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@After("controllerPointCut()")
	public void traceLogWrite(JoinPoint joinPoint) {

		String opMode = Config.getString("operation.mode").toUpperCase();

		if(!TraceLogInfoConstant.OP_MODE_PRD.equals(opMode)) {
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("## Trace Log - Controller(After)");
				}
				/**
				 * 1) DB Write 대상 TraceLogInfo 모델을 ThreadLocal에서 추출
				 * 2) PostgreSQl Write
				 */
				//				TraceLogInfo traceLogInfo = TraceLogThreadLocalManager.getTraceLogInfo();
				//				List<TraceLogParameters> tracelog = traceLogInfo.getTraceLogInfoList();

				/**
				 * Trace log DB Write
				 * traceLogManagerImpl.wirteTraceLog();
				 */
				TraceLogManager traceLogManager = new TraceLogManagerImpl();
				traceLogManager.wirteTraceLog();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				// Close ThreadLocal
				TraceLogThreadLocalManager.clear();
			}
		}
	}

	/**
	 * Target Class(Service class) PointCut 지정 메서드<br>
	 * <br>
	 * @param  N/A
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@Pointcut("execution(* *..service.*Service.*(..))")
	public void servicePointCut() {

	}

	/**
	 * Target Class(Service class) PointCut 지정 메서드 호출 시점에 클래스 및 메서드 정보 추출 클래스<br>
	 * <br>
	 * @param joinPoint the join point
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@Before("servicePointCut()")
	public void serviceAdvice(JoinPoint joinPoint) {

		String opMode = Config.getString("operation.mode").toUpperCase();

		if(!TraceLogInfoConstant.OP_MODE_PRD.equals(opMode)) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("## Trace Log - Service(Before)");
			}

			/**
			 * 1) StackTrace를 이용하여 Callee(Service) Class, Caller(Controller) Class 정보 추출
			 */
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

			String target = joinPoint.getTarget().toString();
			Signature signature = joinPoint.getSignature();
			String servicePkgNm = target.substring(0, target.lastIndexOf('@'));
			String serviceFullPath = new StringBuilder(servicePkgNm).append(".").append(signature.getName()).toString();
			String serviceMethodNm = signature.getName();

			int serviceIdx = 0;
			int curIdx = 0;
			CharSequence service = servicePkgNm;
			for(StackTraceElement ste : stackTrace) {
				if(ste.getClassName().contains(service)) {
					serviceIdx = curIdx;
					break;
				}
				curIdx++;
			}

			int callerIdx = 0;
			for(int idx = serviceIdx; idx < stackTrace.length; idx++) {
				if(stackTrace[idx].getClassName().endsWith("Controller")) {
					callerIdx = idx;
					break;
				}
			}

			if(LOG.isDebugEnabled()) {
				LOG.debug("## Caller is : " + stackTrace[callerIdx].getClassName() + "." + stackTrace[callerIdx].getMethodName());
			}

			String callerPkgNm = stackTrace[callerIdx].getClassName();
			String callerFullPath = stackTrace[callerIdx].getClassName() + "." + stackTrace[callerIdx].getMethodName();
			String callerMethodNm = stackTrace[callerIdx].getMethodName();

			TraceLogInfo traceLogInfo = TraceLogThreadLocalManager.getTraceLogInfo();
			List<TraceLogParameters> traceLogInfoList = traceLogInfo.getTraceLogInfoList();

			TraceLogParameters traceLogParameters = new TraceLogParameters();
			traceLogParameters.setTRAN_ID(traceLogInfo.getUuid());
			traceLogParameters.setCALLEE_HASH_VAL(String.valueOf(serviceFullPath.hashCode()));
			traceLogParameters.setCALLER_HASH_VAL(String.valueOf(callerFullPath.hashCode()));
			traceLogParameters.setCALLEE_PKG_NM(servicePkgNm);
			traceLogParameters.setCALLEE_METHOD_NM(serviceMethodNm);
			traceLogParameters.setCALLER_PKG_NM(callerPkgNm);
			traceLogParameters.setCALLER_METHOD_NM(callerMethodNm);
			traceLogParameters.setCALLEE_TYPE(TraceLogInfoConstant.SERVICE_TYPE_VALUE);

			traceLogInfoList.add(traceLogParameters);
			TraceLogThreadLocalManager.setTraceLogInfo(traceLogInfo);
		}
	}

	/**
	 * Target Class(Module class) PointCut 지정 메서드<br>
	 * <br>
	 * @param N/A
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@Pointcut("execution(* *..module.*Module.*(..))")
	public void modulePointCut() {

	}

	/**
	 * Target Class(Module class) PointCut 지정 메서드 호출 시점에 클래스 및 메서드 정보 추출 클래스<br>
	 * StackTrace 정보를 이용하여 호출자가 Service class 또는 Module class의 메서드 인지를 판별<br>
	 * <br>
	 * @param joinPoint the join point
	 * @return void
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@Before("modulePointCut()")
	public void moduleAdvice(JoinPoint joinPoint) {

		String opMode = Config.getString("operation.mode").toUpperCase();

		if(!TraceLogInfoConstant.OP_MODE_PRD.equals(opMode)) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("## Trace Log - Module(Before)");
			}

			/**
			 * 1) StackTrace를 이용하여 Callee(Module) Class, Caller(Service or Module) Class 정보 추출
			 */
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

			String target = joinPoint.getTarget().toString();
			Signature signature = joinPoint.getSignature();
			String modulePkgNm = target.substring(0, target.lastIndexOf('@'));
			String moduleFullPath = new StringBuilder(modulePkgNm).append(".").append(signature.getName()).toString();
			String moduleMethodNm = signature.getName();

			int moduleIdx = 0;
			int curIdx = 0;
			CharSequence module = modulePkgNm;
			for(StackTraceElement ste : stackTrace) {
				if(ste.getClassName().contains(module)) {
					moduleIdx = curIdx;
					break;
				}
				curIdx++;
			}

			int callerIdx = 0;
			for(int idx = moduleIdx; idx < stackTrace.length; idx++) {
				if(stackTrace[idx].getClassName().endsWith("Module") || stackTrace[idx].getClassName().endsWith("Service")) {
					callerIdx = idx;
					break;
				}
			}

			if(LOG.isDebugEnabled()) {
				LOG.debug("## Caller is : " + stackTrace[callerIdx].getClassName() + "." + stackTrace[callerIdx].getMethodName());
			}

			String callerPkgNm = stackTrace[callerIdx].getClassName();
			String callerFullPath = stackTrace[callerIdx].getClassName() + "." + stackTrace[callerIdx].getMethodName();
			String callerMethodNm = stackTrace[callerIdx].getMethodName();

			TraceLogInfo traceLogInfo = TraceLogThreadLocalManager.getTraceLogInfo();
			List<TraceLogParameters> traceLogInfoList = traceLogInfo.getTraceLogInfoList();

			TraceLogParameters traceLogParameters = new TraceLogParameters();
			traceLogParameters.setTRAN_ID(traceLogInfo.getUuid());
			traceLogParameters.setCALLEE_HASH_VAL(String.valueOf(moduleFullPath.hashCode()));
			traceLogParameters.setCALLER_HASH_VAL(String.valueOf(callerFullPath.hashCode()));
			traceLogParameters.setCALLEE_PKG_NM(modulePkgNm);
			traceLogParameters.setCALLEE_METHOD_NM(moduleMethodNm);
			traceLogParameters.setCALLER_PKG_NM(callerPkgNm);
			traceLogParameters.setCALLER_METHOD_NM(callerMethodNm);
			traceLogParameters.setCALLEE_TYPE(TraceLogInfoConstant.MODULE_TYPE_VALUE);

			traceLogInfoList.add(traceLogParameters);
			TraceLogThreadLocalManager.setTraceLogInfo(traceLogInfo);
		}
	}

	/**
	 * Interceptor interface 클래스를 상속하여 SqlId 획득.
	 * <br>
	 * @param invocation the Invocation
	 * @return Object
	 * @ahthor KimDojin
	 * @since  8. 17.
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		String opMode = Config.getString("operation.mode").toUpperCase();

		Object result = invocation.proceed();

		if(!TraceLogInfoConstant.OP_MODE_PRD.equals(opMode)) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("## Trace Log - SqlMap(Interceptor)");
			}

			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

			/**
			 * 1) sqlId 획득은 StackTrace를 이용하지 않고 intercepter를 이용하여 호출 sqlId만을 획득.
			 * 2) ThreadLocal에 있는 Model의 마지막 항목값을 이용하여 caller 정보 추출.
			 */

			if(LOG.isDebugEnabled()) {
				LOG.debug("## SQL_ID : " + getSqlId(invocation));
			}

			String sqlFullPath = getSqlId(invocation);

			if(!sqlFullPath.isEmpty()) {
				String sqlPkgNm = sqlFullPath.substring(0, sqlFullPath.indexOf('.'));
				String sqlMethodNm = sqlFullPath.substring(sqlFullPath.indexOf('.') + 1, sqlFullPath.length());

				int sqlManagerIdx = 0;
				int curIdx = 0;
				CharSequence module = "frameone.core.common.persistence.SqlManager";
				for(StackTraceElement ste : stackTrace) {
					if(ste.getClassName().contains(module)) {
						sqlManagerIdx = curIdx;
						break;
					}
					curIdx++;
				}

				int callerIdx = 0;
				for(int idx = sqlManagerIdx; idx < stackTrace.length; idx++) {
					if(stackTrace[idx].getClassName().endsWith("Module") || stackTrace[idx].getClassName().endsWith("Service")) {
						callerIdx = idx;
						break;
					}
				}

				String callerPkgNm = stackTrace[callerIdx].getClassName();
				String callerFullPath = stackTrace[callerIdx].getClassName() + "." + stackTrace[callerIdx].getMethodName();
				String callerMethodNm = stackTrace[callerIdx].getMethodName();

				TraceLogInfo traceLogInfo = TraceLogThreadLocalManager.getTraceLogInfo();
				List<TraceLogParameters> traceLogInfoList = traceLogInfo.getTraceLogInfoList();

				if(LOG.isDebugEnabled()) {
					LOG.debug("## traceLogInfoList.size() : " + traceLogInfoList.size());
				}

				TraceLogParameters traceLogParameters = new TraceLogParameters();
				traceLogParameters.setTRAN_ID(traceLogInfo.getUuid());
				traceLogParameters.setCALLEE_HASH_VAL(String.valueOf(sqlFullPath.hashCode()));
				traceLogParameters.setCALLER_HASH_VAL(String.valueOf(callerFullPath.hashCode()));
				traceLogParameters.setCALLEE_PKG_NM(sqlPkgNm);
				traceLogParameters.setCALLEE_METHOD_NM(sqlMethodNm);
				traceLogParameters.setCALLER_PKG_NM(callerPkgNm);
				traceLogParameters.setCALLER_METHOD_NM(callerMethodNm);
				traceLogParameters.setCALLEE_TYPE(TraceLogInfoConstant.REPOSITORY_TYPE_VALUE);

				traceLogInfoList.add(traceLogParameters);
				TraceLogThreadLocalManager.setTraceLogInfo(traceLogInfo);

				// 메모리 누수로 로컬 객체 초기화.
				traceLogInfoList.clear();
				traceLogInfo.remove();

			} else {
				if(LOG.isDebugEnabled()) {
					LOG.debug("## Not Found SQL-ID");
				}
			}
		}
		return result;
	}

	private String getSqlId(Invocation invocation) throws Throwable {
		StatementHandler handler = (StatementHandler)invocation.getTarget();
		DefaultParameterHandler parameterHandler = (DefaultParameterHandler)handler.getParameterHandler();
		Field field = parameterHandler.getClass().getDeclaredField("mappedStatement");
		field.setAccessible(true);
		MappedStatement mappedStatement = (MappedStatement)field.get(parameterHandler);

		return mappedStatement.getId();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties arg0) {
		//		To change body of implemented methods use File | Settings | File Templates.
	}
}
