/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * KongJungil		2010. 3. 2.		    First Draft.
 */
package goodseed.core.common.utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;
import goodseed.core.exception.SystemException;
import goodseed.core.exception.UserHandleException;

/**
 * The class ReflectionUtils<br>
 * <br>
 * 리플렉션과 관련된 util class
 * <br>
 * @author jay
 * @version 1.0
 * 
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

	private static final Log LOG = LogFactory.getLog(ReflectionUtils.class);

	/**
	 * 클래스 이름으로 Class 타입정보를 반환한다.
	 * @param classname 클래스아룸
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(String classname) throws ClassNotFoundException {
		Class<?> clazz = null;
		clazz = Class.forName(classname, false, Thread.currentThread().getContextClassLoader());
		return clazz;
	}

	/**
	 * 클래스에 정의된 field 정보를 반환한다.
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class clazz, String fieldName) {
		return getField(clazz, fieldName, false);
	}

	/**
	 * 클래스의 필드이름으로 field 타입을 반환한다.
	 * @param clazz 클래스
	 * @param fieldName 필드명
	 * @param findInSuperClass 슈퍼클래스가 있는지 여부
	 * @return
	 */
	public static Field getField(Class clazz, String fieldName, Boolean findInSuperClass) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch(SecurityException e) {
			throw new SecurityException(e);
		} catch(NoSuchFieldException e) {
			if(findInSuperClass) {
				field = ReflectionUtils.getField(clazz.getSuperclass(), fieldName, findInSuperClass);
			} else {
				throw new IllegalStateException(e);
			}
		}
		return field;
	}

	/**
	 * class 안의 object의 값을 반환한다.
	 * @param clazz 클래스
	 * @param object 리턴값
	 * @param fieldName 필드명
	 * @return
	 */
	public static Object getValue(Class clazz, Object object, String fieldName) {
		return getValue(clazz, object, fieldName, false);
	}

	/**
	 * class 안의 field의 값을 반환한다.
	 * @param clazz 클래스
	 * @param object 오브젝트
	 * @param fieldName 필드명
	 * @param findInSuperClass 상위클래스에서 찾을지 여부
	 * @return
	 */
	public static Object getValue(Class clazz, Object object, String fieldName, Boolean findInSuperClass) {
		Field field = getField(clazz, fieldName, findInSuperClass);
		field.setAccessible(true);

		Object result = null;
		try {
			result = field.get(object);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch(IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	/**
	 * 해당 Class의 필드 Type을 반환한다.
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Class getType(Class clazz, String fieldName) {
		Field field = getField(clazz, fieldName);
		return field.getType();
	}

	/**
	 * Class의 field내에 지정된 Object의 실제 하위 Type을 반환한다.
	 * @param clazz 클래스
	 * @param object 오브젝트
	 * @param fieldName 필드명
	 * @return Class 클래스 타입
	 */
	public static Class getSubType(Class clazz, Object object, String fieldName) {
		Field field = getField(clazz, fieldName);
		field.setAccessible(true);

		Object realObject = null;
		try {
			realObject = field.get(object);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch(IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

		return realObject.getClass();
	}

	/**
	 * Class내에 정의된 Method를 호출한다
	 * @param clazz 클래스
	 * @param methodName 호출할 메소드
	 * @param object 호출할때 넘길 인자
	 * @return Object 메소드 호출한 후의 결과 Object
	 */
	public static Object callMethod(Class clazz, String methodName, Object... object) {
		Object result = null;
		List<Method> methods = Arrays.asList(clazz.getMethods());
		for(Method method : methods) {
			if(method.getName().equals(methodName)){
				
				try {
					if(method.getReturnType() != null) {
						result = method.invoke(clazz.newInstance(), object);
					} else {
						method.invoke(clazz.newInstance(), object);
					}
				} catch(InstantiationException e) {
					if(LOG.isErrorEnabled()) {
						LOG.error(e, e);
					}
				} catch(IllegalArgumentException e) {
					throw new IllegalArgumentException(e);
				} catch(IllegalAccessException e) {
					throw new IllegalStateException(e);
				} catch(InvocationTargetException e) {
					throw new IllegalStateException(e);
				}
			}
		}
		return result;
	}

	/**
	 * 클래스의 해당 메소드를 반환한다.
	 * @param clazz 클래스 
	 * @param methodName 반환하고자하는 메소드명
	 * @param params 실행할때 사용하는 파라미터
	 * @return Method 메소드
	 */
	public static Method getMethod(Class clazz, String methodName, Object... params) {
		Method result = null;
		Class[] clazzes = new Class[params.length];
		for(int i = 0; i < params.length; i++) {
			clazzes[i] = params[i].getClass();
		}
		try {
			result = clazz.getMethod(methodName, clazzes);
		} catch(SecurityException e) {
			throw new SecurityException(e);
		} catch(NoSuchMethodException e) {
			throw new SystemException(e);
		}
		return result;
	}

	/**
	 * 메소드를 반환한다.
	 * @param clazz
	 * @param methodName
	 * @param paramType
	 * @return
	 */
	public static Method getMethod(Class clazz, String methodName, Class paramType) {
		Method result = null;
		try {
			result = clazz.getMethod(methodName, paramType);
		} catch(SecurityException e) {
			throw new SecurityException(e);
		} catch(NoSuchMethodException e) {
			throw new SystemException(e);
		}
		return result;
	}
	/**
	 * @deprecated
	 */
	@Deprecated
	public static Object invokeMethod(Class clazz, String beanName, String methodName, Object... params) {
		Object result = null;
		Method method = getMethod(clazz, methodName, params);
		try {
			if(method.getReturnType() != null) {
				result = method.invoke(GoodseedContextLoaderAdapter.getBean(beanName), params);
			} else {
				method.invoke(GoodseedContextLoaderAdapter.getBean(beanName), params);
			}
		} catch(Exception e) {
			if(e.getCause() instanceof UserHandleException) {
				throw (UserHandleException)e.getCause();
			} else if(e.getCause() instanceof SystemException) {
				throw (SystemException)e.getCause();
			} else {
				throw new SystemException(e);
			}
		}
		
		return result;
	}

	/**
	 * Context에 정의된 bean객체를 획득하여 Method를 호출한다.
	 * @param beanName
	 * @param methodName
	 * @param params
	 * @return
	 */
	public static Object invokeMethod(String beanName, String methodName, Object... params) {
		Object result = null;
		try {
			Object bean = GoodseedContextLoaderAdapter.getBean(beanName);
			Method method = getMethod(bean.getClass(), methodName, params);
			if(method.getReturnType() != null) {
				result = method.invoke(GoodseedContextLoaderAdapter.getBean(beanName), params);
			} else {
				method.invoke(GoodseedContextLoaderAdapter.getBean(beanName), params);
			}
		} catch(Exception e) {
			if(e.getCause() instanceof UserHandleException) {
				throw (UserHandleException)e.getCause();
			} else if(e.getCause() instanceof SystemException) {
				throw (SystemException)e.getCause();
			} else {
				throw new SystemException(e);
			}
		}
		
		return result;
	}

	/**
	 * Context에 정의된 bean객체를 획득하여 Method를 호출한다.
	 * @param beanName
	 * @param methodName
	 * @param params
	 * @param paramType
	 * @return
	 */
	
	public static Object invokeMethod(String beanName, String methodName, Object params, Class<?> paramType) {
		Object result = null;
		try {
			Object bean = GoodseedContextLoaderAdapter.getBean(beanName);
			Method method = getMethod(bean.getClass(), methodName, paramType);
			if(method.getReturnType() != null) {
				result = method.invoke(GoodseedContextLoaderAdapter.getBean(beanName), params);
			} else {
				method.invoke(GoodseedContextLoaderAdapter.getBean(beanName), params);
			}
		} catch(Exception e) {
			if(e.getCause() instanceof UserHandleException) {
				throw (UserHandleException)e.getCause();
			} else if(e.getCause() instanceof SystemException) {
				throw (SystemException)e.getCause();
			} else {
				throw new SystemException(e);
			}
		}
		
		return result;
	}
}
