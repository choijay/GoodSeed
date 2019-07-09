/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * LeeHyeonJae		2013. 6. 14.		First Draft.
 */
package goodseed.core.common.utility;

/**
 * The interface XssShield<br>
 * <br>
 * XSS(Cross Site Scripting) 공격을 방어할 목적으로 만든 인터페이스<br>
 * <br>
 * 
 * @author jay
 */
public interface XssShield {

	/**
	 * 원본문자열을 받아서 XSS 공격이 예상되는 패턴을 replace 처리한 문자열을 리턴한다.<br>
	 * <br>
	 * @param value
	 * @return
	 */
	String stripXss(String value);
}
