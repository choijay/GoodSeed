/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of Systems
 */
package goodseed.core.common.service;

import org.springframework.util.StringUtils;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;

/**
 * The class FrameOneService
 *
 * @author jay
 * @version 1.0
 *
 */
public class GoodseedService {

	/**
	 * SqlManager 인스턴스를 사용하고자 하는 경우 이 메소드를 호출한다.<br>
	 * 기본 SqlManager를 사용하는 경우 getSqlManager() 메소드를 사용하도록 한다.<br>
	 *
	 * @param dataSourceId 획득하고자 하는 SqlManager의 dataSourceId를 입력한다.<br>
	 * dataSourceId를 모르는 경우, 팀내 AA(SA) 또는 시스템 관리자에게 문의한다.
	 *
	 * @return 입력한 dataSourceId에 대항하는 SqlManager 인스턴스를 반환한다.
	 */
	public SqlManager getSqlManager(String dataSourceId) {
		return (SqlManager)SqlManagerFactory.getSqlManager(dataSourceId);
	}

	/**
	 * SqlManager 인스턴스를 사용하고자 하는 경우 이 메소드를 호출한다.<br>
	 *
	 * @return 기본 SqlManager 인스턴스를 반환한다.
	 */
	public SqlManager getSqlManager() {
		return (SqlManager)SqlManagerFactory.getSqlManager(GoodseedConstants.DEFAULT_SQL_MANAGER);
	}

	/**
	 * Spring Container에서 관리되는 현재 서비스의 인스턴스를 가져온다.<br>
	 * AOP가 적용되어 있을 경우 AOP Proxy 객체가 반환된다.<br>
	 * 자신을 참조하여 AOP를 적용하고 하는 경우 사용하도록하며, 자신을 참조하는 경우가 아닌 경우, 멤버 변수로 선언하여 호출하도록 한다.<br>
	 * 
	 * 새로운 트랜잭션을 사용하고자 하는 경우 FrameOne 2.0 에서는 getThis 를 사용하지 않고, 다음의 방법을 사용하도록 한다.
	 * 1. SqlManager.insertRequiredNew, updateRequiredNew, deleteRequiredNew  메소드를 사용하여 개별 트랜잭션으로 처리하도록 함
	 * 2. FrameOneModule 의 두번째 인자로 propagation 을 넘겨서 새로운 트랜잭션을 처리하도록 함
	 *<br>
	 * @param <T>
	 * @return 현재 서비스의 인스턴스
	 * TODO 클래스명이 연속되는 대문자로 시작하는 경우에 대한 처리 필요.
	 * @deprecated
	 */
	@Deprecated
	protected <T extends GoodseedService> T getThis() {
		String fqn = this.getClass().getName();
		return (T)GoodseedContextLoaderAdapter.getBean(StringUtils.uncapitalize(fqn.substring(fqn.lastIndexOf('.') + 1)));
	}
}
