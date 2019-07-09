/*
 *
 * Copyright (c) 2012 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed.
 *
 * If you have any questions, please contact FrameOne team.
 *
 */
package goodseed.core.common.model.factory;

import java.util.List;

import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.GoodseedHtmlDataset;
import goodseed.core.common.model.GoodseedHtmlNoXssDataset;
import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;

/**
 * The class AbstractDatasetFactory<br>
 * <br>
 * 추상 DatasetFactory 클래스.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 3. 23.
 *
 */
public abstract class AbstractDatasetFactory {

	private static GoodseedHtmlDatasetFactory frameOneHtmlDatasetFactory = null;

	/**
	 * DatasetFactory factory method.
	 *
	 * @ahthor KongJungil
	 * @since 3. 23.
	 *
	 * @param dataset
	 * @return
	 */
	public static <T extends GoodseedDataset> AbstractDatasetFactory getDatasetFactory(Class<T> clazz) {
		if(clazz.equals(GoodseedHtmlDataset.class)) {
			return frameOneHtmlDatasetFactory.getInstance();
		} else if(clazz.equals(GoodseedHtmlNoXssDataset.class)) {
			return frameOneHtmlDatasetFactory.getInstance();
		}
		return null;
	}

	/**
	 * DatasetFactory factory method.
	 *
	 * @ahthor KongJungil
	 * @since 3. 23.
	 *
	 * @param dataset
	 * @return
	 */
	public static <T extends Parameters> AbstractDatasetFactory getDatasetFactoryByParameters(Class<T> clazz) {
		if(clazz.equals(HtmlParameters.class)) {
			return frameOneHtmlDatasetFactory.getInstance();
		}
		return null;
	}

	/**
	 * DatasetFactory factory method.
	 *
	 * @ahthor KongJungil
	 * @since 3. 23.
	 *
	 * @param dataset
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AbstractDatasetFactory getDatasetFactory(GoodseedDataset dataset) {
		Class clazz = null;
		GoodseedDataset originalDataset = dataset.getOriginalDataset();
		if(originalDataset != null) {
			clazz = originalDataset.getClass();
		} else {
			clazz = dataset.getClass();
		}
		return getDatasetFactory(clazz);
	}

	/**
	 * DatasetFactory factory method.
	 *
	 * @ahthor KongJungil
	 * @since 3. 23.
	 *
	 * @param dataset
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AbstractDatasetFactory getDatasetFactoryByParameters(Parameters parameters) {
		Class clazz = null;
		Parameters originalParameters = parameters.getOriginalParameters();
		if(originalParameters != null) {
			clazz = originalParameters.getClass();
		} else {
			clazz = parameters.getClass();
		}
		return getDatasetFactoryByParameters(clazz);
	}

	/**
	 * Default DatasetFactory factory method.
	 *
	 * @ahthor KongJungil
	 * @since 3. 23.
	 *
	 * @param dataset
	 * @return
	 */
	public static AbstractDatasetFactory getDefaultDatasetFactory() {
		return GoodseedHtmlDatasetFactory.getInstance();
	}

	/**
	 * Object 를 Dataset 으로 변환.<br/>
	 * 기본 Dataset 이름은 "output"를 사용한다.
	 *
	 * @param Dataset으로 변환할 object 인스턴스
	 * @return object의 내용을 저장한 Dataset 인스턴스
	 */
	public abstract GoodseedDataset makeDataset(Object object);

	/**
	 * Object 를 Dataset 으로 변환한다.<br>
	 * Object의 타입이 List인 경우 List 내의 element를 추출하여, Dataset의 행으로 변환하며,<br>
	 * List 형이 아닌 경우, 해당 Object의 Property를 추출하여 1개 행을 가진 Dataset으로 변환하다.
	 *
	 * @param object
	 * @param datasetName
	 * @return Object의 내용을 저장한 Dataset 인스턴스
	 */
	public abstract GoodseedDataset makeDataset(Object object, String datasetName);

	/**
	 * List&lt;?&gt; 를 Dataset 으로 변환한다.
	 *
	 * @param list
	 * @param datasetName
	 * @return List 객체를 변환한 GoodseedDataset 객체
	 */
	public abstract GoodseedDataset makeDataset(List<?> list, String datasetName);

	/**
	 * 삭제된 행과 수정된 행(또는 전체 행)을 입력받아 FrameOneDataset을 생성한다.
	 *
	 * @ahthor KongJungil
	 * @since 3. 26.
	 *
	 * @param list
	 * @param datasetName
	 * @return
	 */
	public abstract GoodseedDataset makeDataset(List<?> data, List<?> deletedData, String datasetName);

}
