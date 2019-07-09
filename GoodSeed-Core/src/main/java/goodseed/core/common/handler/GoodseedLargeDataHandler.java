package goodseed.core.common.handler;

import org.apache.ibatis.session.ResultHandler;

import goodseed.core.common.model.Parameters;

/**
 *
 * The class FrameOneLargeDataHandler<br>
 * <br>
 * 대량 데이터를 처리하기 위한 인터페이스<br>
 * 인터페이스를 상속받아서 다양한 채널 통합시에 활용<br>
 * <br>
 * Copyright (c) 2016 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 2.0
 * @since  10. 24.
 *
 */
public interface GoodseedLargeDataHandler extends ResultHandler{

	/**
	 *
	 * ParamMap 를 세팅<br>
	 * <br>
	 * @param params
	 * @ahthor bestdriver
	 * @since  10. 24.
	 */
	public void setParams(Parameters params);

	/**
	 *
	 * 데이터를 모두처리하고 자원해제하는 메소드<br>
	 * <br>
	 * @ahthor bestdriver
	 * @since  10. 24.
	 */
	public void close();

}
