/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.observer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class GoodseedConfigSubject
 * <br>
 * Subject를 구현하는 Config관련 Observer를 관리(regist/remove/notify)하는 클래스
 * <br>
 * 
 * @author jay
 *
 */
public class GoodseedConfigSubject implements Subject {

	private static final Log LOG = LogFactory.getLog(GoodseedConfigSubject.class);

	private List<Object> observers;
	
	public GoodseedConfigSubject(List<Object> objectList) {
		this.observers = new ArrayList<Object>();
		
		for(Object obj : objectList) {
			Observer o = (Observer) obj;
			if(LOG.isDebugEnabled()) {
				LOG.debug("Observer [" + o.getClass().getName() + "] class registered.");
			}
			registerObserver(o);
		}
	}
	
	public List<Object> getObservers() {
		return observers;
	}

	public void setObservers(List<Object> observers) {
		this.observers = observers;
	}
	
	@Override
	public void registerObserver(Observer o) {
		this.observers.add(o);

	}

	@Override
	public void removeObserver(Observer o) {
		int i = this.observers.size();
		if (i >= 0) {
			this.observers.remove(o);
		}

	}

	@Override
	public void notifyObserver() {
		Observer observer;
		for (int i = 0; i < this.observers.size(); i++) {
			observer = (Observer) this.observers.get(i);
			observer.update();
		}

	}

}
