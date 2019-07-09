/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.observer;

/**
 * The class Observer
 * <br>
 * Observer 인터페이스
 * <br>
 * 
 * @author jay
 *
 */
public interface Observer {

	/**
	 * notified 된 시점에 수행하는 함수 
	 */
	public void update();
}
