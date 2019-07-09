/**
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 */
package goodseed.core.utility.uuid.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class Resource<br>
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
 * @since  8. 11.
 *
 */
public final class Resource {

	private static final Log LOG = LogFactory.getLog(Resource.class);

	private static Map<String, String> insteadOfClose = new ConcurrentHashMap<String, String>(13);

	private static Map<String, String> beforeClose = new ConcurrentHashMap<String, String>(5);

	private static Map<String, String> afterClose = new ConcurrentHashMap<String, String>(5);

	static {
		beforeClose("javax.jms.Connection", "stop");
		beforeClose("javax.imageio.ImageWriter", "reset");
		beforeClose("javax.imageio.stream.ImageInputStream", "flush");

		insteadOfClose("com.eaio.nativecall.NativeCall", "destroy");
		insteadOfClose("com.jcraft.jsch.Channel", "disconnect");
		insteadOfClose("de.intarsys.cwt.environment.IGraphicsContext", "dispose");
		insteadOfClose("groovyx.net.http.HTTPBuilder", "shutdown");
		insteadOfClose("java.lang.Process", "destroy");
		insteadOfClose("javax.imageio.ImageReader", "dispose");
		insteadOfClose("javax.imageio.ImageWriter", "dispose");
		insteadOfClose("org.apache.http.impl.client.AbstractHttpClient", "shutdown");
		insteadOfClose("org.infinispan.Cache", "stop");
		insteadOfClose("org.infinispan.manager.DefaultCacheManager", "stop");
	}

	/**
	 * No instances needed.
	 */
	private Resource() {
		super();
	}

	/**
	 * Call a certain <code>void</code> method before calling the <code>close</code> method.
	 *
	 * @param clazz the class, can be an interface, too, may not be <code>null</code>
	 * @param method the method name, may not be <code>null</code>
	 */
	public static void beforeClose(Class<?> clazz, String method) {
		beforeClose(clazz.getName(), method);
	}

	/**
	 * Call a certain <code>void</code> method before calling the <code>close</code> method.
	 *
	 * @param className the class name, may not be <code>null</code>
	 * @param method the method name, may not be <code>null</code>
	 */
	public static void beforeClose(String className, String method) {
		beforeClose.put(className, method);
	}

	/**
	 * Call a certain <code>void</code> method after calling the <code>close</code> method.
	 *
	 * @param clazz the class, can be an interface, too, may not be <code>null</code>
	 * @param method the method name, may not be <code>null</code>
	 */
	public static void afterClose(Class<?> clazz, String method) {
		afterClose(clazz.getName(), method);
	}

	/**
	 * Call a certain <code>void</code> method before calling the <code>close</code> method.
	 *
	 * @param className the class name, may not be <code>null</code>
	 * @param method the method name, may not be <code>null</code>
	 */
	public static void afterClose(String className, String method) {
		afterClose.put(className, method);
	}

	/**
	 * Call a certain <code>void</code> method instead of calling the <code>close</code> method.
	 *
	 * @param clazz the class name, may not be <code>null</code>
	 * @param method the method name, may not be <code>null</code>
	 */
	public static void insteadOfClose(Class<?> clazz, String method) {
		insteadOfClose(clazz.getName(), method);
	}

	/**
	 * Call a certain <code>void</code> method instead of calling the <code>close</code> method.
	 *
	 * @param className the class name, may not be <code>null</code>
	 * @param method the method name, may not be <code>null</code>
	 */
	public static void insteadOfClose(String className, String method) {
		insteadOfClose.put(className, method);
	}

	/**
	 * Closes objects in the order they are given.
	 *
	 * @param objects any number of objects, may be <code>null</code> or individual objects may be <code>null</code>
	 */
	public static void close(Object... objects) {
		if(objects == null) {
			return;
		}
		for(Object object : objects) {
			if(object != null) {
				callFromMap(beforeClose, object);
				if(!callFromMap(insteadOfClose, object)) {
					callVoidMethod(object, "close");
				}
				callFromMap(afterClose, object);
			}
		}
	}

	private static boolean callFromMap(Map<String, String> map, Object object) {
		return callFromMap(map, object, object.getClass());
	}

	private static boolean callFromMap(Map<String, String> map, Object object, Class<?> currClass) {
		String currentClassName = currClass.getName();

		// First check class itself
		String voidMethod = map == null ? null : map.get(currentClassName);
		if(voidMethod != null) {
			callVoidMethod(object, voidMethod);
			return true;
		}
		// Then check interfaces
		else if(callFromMapFromInterfaces(map, object, currClass)) {
			return true;
		}
		// Finally check base classes recursively
		else if(hasSuperclass(currClass)) {
			if(callFromMap(map, object, currClass.getSuperclass())) {
				return true;
			}
		}
		return false;
	}

	private static boolean callFromMapFromInterfaces(Map<String, String> map, Object object, Class<?> currClass) {
		boolean atLeastOneMethodCalled = false;
		for(Class<?> currentInterface : currClass.getInterfaces()) {
			if(callFromMap(map, object, currentInterface)) {
				atLeastOneMethodCalled = true;
			}
		}
		return atLeastOneMethodCalled;
	}

	private static boolean hasSuperclass(Class<?> clazz) {
		return clazz != null && clazz != Object.class && clazz.getSuperclass() != null;
	}

	private static void callVoidMethod(Object object, String method) {
		try {
			Method m = object.getClass().getMethod(method, new Class<?>[]{});
			m.invoke(object, new Object[]{});
		} catch(NoSuchMethodException ex) {
			log(object, ex);
		} catch(IllegalArgumentException ex) {
			log(object, ex);
		} catch(IllegalAccessException ex) {
			log(object, ex);
		} catch(InvocationTargetException ex) {
			log(object, ex.getCause());
		} catch(SecurityException ex) {
			log(object, ex);
		}
	}

	private static void log(Object object, Throwable throwable) {
		if(LOG.isTraceEnabled()) {
			LOG.warn(object.getClass().getName(), throwable);
		} else if(LOG.isDebugEnabled()) {
			LOG.warn(object.getClass().getName() + ": " + throwable.getClass().getName() + ": " + throwable.getLocalizedMessage());
		}
	}
}
