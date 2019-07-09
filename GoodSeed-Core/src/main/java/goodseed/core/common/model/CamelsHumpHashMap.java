/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import goodseed.core.utility.string.StringUtil;

/**
 * The class CamelsHumpHashMap<br>
 * <br>
 * @author jay
 * @version 1.0
 *
 */
public class CamelsHumpHashMap extends HashMap {

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = 2421077081558053193L;

	private static ConcurrentHashMap<String, String> cachedConversionMap = new ConcurrentHashMap<String, String>(1000);

	/**
	 * the default constructor.
	 */
	public CamelsHumpHashMap() {
		super();
	}

	@Override
	public Object put(Object key, Object value) {
		String columnName = null;
		if(cachedConversionMap.containsKey(key)) {
			columnName = cachedConversionMap.get(key);
		} else {
			columnName = StringUtils.uncapitalize(StringUtil.removeAndHump(((String)key).toLowerCase()));
			cachedConversionMap.put((String)key, columnName);
		}
		return super.put(columnName, value);
	}

}
