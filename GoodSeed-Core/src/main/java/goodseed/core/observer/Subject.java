/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.observer;

/**
 * The class Subject
 * <br>
 * Subject 인터페이스
 * <br>
 * 
 * @author jay
 *
 */
public interface Subject {

	/**
	 * Observer 등록
	 * @param o
	 */
	public void registerObserver(Observer o);
	
	/**
	 * Observer 제거
	 * @param o
	 */
	public void removeObserver(Observer o);
	
	/**
	 * Observer 에게 알림
	 * @param o
	 */
	public void notifyObserver();
}
