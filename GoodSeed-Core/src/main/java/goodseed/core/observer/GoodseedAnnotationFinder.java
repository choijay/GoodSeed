package goodseed.core.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import goodseed.core.common.GoodseedConstants;

/**
*
* The class FrameOneAnnotationFinder<br>
* <br>
* Annotation 검색 기능
* <br>
* Copyright (c) 2014 GoodSeed<br>
* All rights reserved.<br>
* <br>
* This software is the proprietary information of GoodSeed<br>
* <br>
* @author jay
* @version 3.0
* @since  03. 20.
*
*/
public class GoodseedAnnotationFinder {

//	private static String basePackages = "cj:frameone";
	private static ServletContext servletContext;
	private String configLocations;
	
	public GoodseedAnnotationFinder(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	public GoodseedAnnotationFinder(String configLocations) {
		this.configLocations = configLocations;
	}
	
	

	/**
	 * 해당하는 annotation 을 가지는 클래스를 찾아 List에 담아서 리턴
	 * @param annotationClass
	 * @return
	 */
	public List<Object> find(Class annotationClass) {

		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		Map<String, Object> beans = context.getBeansWithAnnotation(annotationClass);
		
		List<Object> objList = new ArrayList<Object>();
		for(String key : beans.keySet()){
			objList.add(beans.get(key));
        }
		
		return objList;
	}

}
