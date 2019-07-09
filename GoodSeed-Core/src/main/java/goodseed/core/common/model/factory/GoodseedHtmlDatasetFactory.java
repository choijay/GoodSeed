/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.model.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.GoodseedHtmlDataset;
import goodseed.core.exception.SystemException;

/**
 * The class DatasetFactory<br>
 * <br>
 * GoodseedDataset 생성을 담당하는 Factory 클래스.<br>
 * <br>
 *
 * @author jay
 *
 */
@SuppressWarnings("unchecked")
public class GoodseedHtmlDatasetFactory extends AbstractDatasetFactory {

	private static final Log LOG = LogFactory.getLog(GoodseedHtmlDatasetFactory.class);

	private static GoodseedHtmlDatasetFactory goodseedHtmlDatasetFactory = null;

	public static GoodseedHtmlDatasetFactory getInstance() {
		if(goodseedHtmlDatasetFactory == null) {
			goodseedHtmlDatasetFactory = new GoodseedHtmlDatasetFactory();
		}
		return goodseedHtmlDatasetFactory;

	}

	@Override
	public GoodseedDataset makeDataset(Object object) {
		return makeDataset(object, "output");
	}

	/**
	 * Object 를 Dataset 으로 변환한다.<br>
	 * Object의 타입이 List인 경우 List 내의 element를 추출하여, Dataset의 행으로 변환하며,<br>
	 * List 형이 아닌 경우, 해당 Object의 Property를 추출하여 1개 행을 가진 Dataset으로 변환하다.
	 *
	 * @param object
	 * @param datasetName
	 * @return Object의 내용을 저장한 Dataset 인스턴스
	 */
	public GoodseedHtmlDataset makeDataset(Object object, String datasetName) {

		if(datasetName == null) {
			try {
				throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
			} catch(Exception e) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e);
				}
				e.printStackTrace();
			}
		}

		return makeDataset((List)object, datasetName);
	}

	/**
	 * List&lt;?&gt; 를 Dataset 으로 변환한다.
	 *
	 * @param list
	 * @param datasetName
	 * @return TODO
	 * @throws Exception
	 */
	@Override
	public GoodseedHtmlDataset makeDataset(List<?> list, String datasetName) {

		GoodseedHtmlDataset dataSet = null;

		dataSet = new GoodseedHtmlDataset(datasetName, list, null);

		return dataSet;
	}

	/**
	 * datasetList 와 variableList 에 있는 dataset, variable data를 map 에 담는다.
	 *
	 * @param datasetList
	 * @param variableList
	 * @return TODO
	 */
//	public HtmlParameters putDatasetAndVariable(DatasetList datasetList, VariableList variableList) {
//		try {
//			throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			if(LOG.isErrorEnabled()) {
//				LOG.error("Exception", e);
//			}
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * dataset 의 data를 map 에 담는다.
	 *
	 * @param dataset
	 * @param row
	 * @return TODO
	 */
	public Map convertDatasetToMap(ArrayList list, int row) {
		return (Map)list.get(row);
	}

	/**
	 * dataset 의 delete 된 data를 map에 담는다.
	 *
	 * @param dataset
	 * @param row
	 * @return TODO
	 */
	public Map convertDatasetDelRowToMap(GoodseedHtmlDataset dataset, int row) {
		try {
			throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * DatasetList 의 dataset 을 map 에 담는다.
	 *
	 * @param datasetMap
	 * @param datasetList
	 */
//	public void convertDatasetListToMap(Map<String, Map<Integer, LinkedMap>> datasetMap, DatasetList datasetList) {
//		try {
//			throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			if(LOG.isErrorEnabled()) {
//				LOG.error("Exception", e);
//			}
//			e.printStackTrace();
//		}
//	}

	@Override
	public GoodseedHtmlDataset makeDataset(List<?> data, List<?> deletedData, String datasetName) {
		GoodseedHtmlDataset dataSet = null;
		String resultDatasetName;

		resultDatasetName = StringUtils.defaultString(datasetName, "output");

		dataSet = new GoodseedHtmlDataset(resultDatasetName, data, deletedData);

		return dataSet;
	}

	/**
	 * GoodseedDataset 형태를 list 형태로 변환
	 *
	 * @ahthor CJ
	 * @since 10. 11.
	 *
	 * @param Parameters
	 * @param dataset
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> convertGoodseedDataset2List(GoodseedDataset dataset) {
		try {
			throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * List 형태를 Frameone 의 DataSet 으로 변환
	 *
	 * @ahthor CJ
	 * @since 10. 11.
	 *
	 * @param Parameters
	 * @param list
	 * @throws Exception
	 */
	public static GoodseedDataset convertListToGoodseedDataset(Object list) {
		try {
			throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
		}

		return null;
	}

}
