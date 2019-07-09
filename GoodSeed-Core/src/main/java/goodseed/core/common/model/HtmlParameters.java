/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.model;

import java.util.HashMap;
import java.util.Map;

import goodseed.core.common.GoodseedConstants;

/**
 * 
 * The class HtmlParameters<br>
 * <br>
 * Html UI에서 사용하는 Parameters 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2011 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 1.0
 * @since 4. 26.
 *
 */
@SuppressWarnings("unchecked")
public class HtmlParameters extends BaseParameters implements Parameters {

	/**
	 * variable을 저장하는 Map.
	 */
	private Map variableMap = null;

	/**
	 * HtmlParameters 생성자 .
	 *
	 * @author jay
	 * @since 4. 26.
	 *
	 */
	public HtmlParameters() {
		super();
		this.variableMap = new HashMap();
		//ERROR_CODE , ERROR_MESSAGE 초기화 
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SUCCESS);
		setVariable(GoodseedConstants.ERROR_MESSAGE_TEXT, "");

	}

	/**
	 * Parameters 에서 입력한 datasetName에 해당하는 FrameOneHtmlDataset 을 가져 온다.
	 */
	@Override
	public GoodseedHtmlDataset getGoodseedDataset(String datasetName) {
		Object obj = get(datasetName);
		if(obj instanceof GoodseedHtmlDataset) {
			GoodseedHtmlDataset data = (GoodseedHtmlDataset)obj;
			if(data != null && this.size() > 0) {
				data.putAll(variableMap);
			}
			return data;
		} else {
			return null;
		}
	}

	/**
	 *  FrameOneHtmlDataset 을 생성하여 리턴한다.
	 */
	@Override
	public GoodseedHtmlDataset getGoodseedDatasetInstance() {
		GoodseedHtmlDataset result = new GoodseedHtmlDataset();
		result.putAll(variableMap);
		return result;
	}

	/**
	 * HtmlParameters 자체 Map에 입력받은 key에 매핑되는 variable 값을 put하고,<br>.
	 * 멤버변수인 VariableMap에도 put한다.
	 */
	@Override
	public final void setVariable(String key, Object variable) {
		put(key, variable);
		variableMap.put(key, variable);
	}

	/**
	 *	HtmlParameters 자체가 original parameters 이므로 본 클래스에서는 의미가 없어 null을 리턴한다.<br>
	 */
	@Override
	public Parameters getOriginalParameters() {
		return null;
	}

	/**
	 * 멤버변수인 VariableMap 을 리턴한다.
	 */
	public Map getVariableMap() {
		return this.variableMap;
	}
}
