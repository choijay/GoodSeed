/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.mvc.model;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class BaseModel
 * 
 * @author jay
 * @version 1.0
 * 
 */
public class BaseModel implements Serializable {

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = -3499166491792800410L;

	private static final Log LOG = LogFactory.getLog(BaseModel.class);

	@Override
	public String toString() {
		return getReflectionToString(this);
	}

	@SuppressWarnings("unchecked")
	public static String getReflectionToString(Object object) {
		Class clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder returnString = new StringBuilder();
		for(Field field : fields) {
			field.setAccessible(true);
			returnString.append(field.getName());
			returnString.append(" = ");
			try {
				returnString.append(field.get(object));
			} catch(IllegalArgumentException e) {
				returnString.append("IllegalArgumentException occured!!");
				returnString.append(e.toString());
				LOG.error("exception", e);
			} catch(IllegalAccessException e) {
				returnString.append("IllegalAccessException occured!!");
				returnString.append(e.toString());
				LOG.error("exception", e);
			}
			returnString.append(";");
			returnString.append("\n");
		}
		return returnString.toString();
	}
}
