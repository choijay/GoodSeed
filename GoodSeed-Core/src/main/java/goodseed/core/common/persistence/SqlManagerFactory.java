/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.persistence;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;

/**
 * The class SqlManagerFactory
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public abstract class SqlManagerFactory {

	public static SqlManager getSqlManager(String dataSourceId) {
		return (SqlManager)GoodseedContextLoaderAdapter.getBean(dataSourceId);
	}

	public static SqlManager getSqlManager() {
		return (SqlManager)GoodseedContextLoaderAdapter.getBean(GoodseedConstants.DEFAULT_SQL_MANAGER);
	}

}
