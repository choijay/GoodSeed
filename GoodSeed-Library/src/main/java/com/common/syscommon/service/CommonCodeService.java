/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package com.common.syscommon.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.common.syscommon.utility.CodeUtil;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.GoodseedHtmlDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.AbstractDatasetFactory;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.MessageResourcePathBeanFactory;

/**
 * The class ProgramService<br>
 * <br>
 * 		상위공통코드 와 공통코드를 조회/등록한다.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 *
 */
@Service
@SuppressWarnings("unchecked")
public class CommonCodeService extends GoodseedService {

	private static final Log LOG = LogFactory.getLog(CommonCodeService.class);

	@Autowired
	private MessageResourcePathBeanFactory messageResourcePathList;

	/**
	 *
	 * 공통그룹코드 정보를 조회한다.<br>
	 *
	 * <br>
	 * @param inParams
	 * @return outParams
	 * @author jay
	 * @since  4. 11.
	 */
	public Parameters getCommonGroupCodeList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset searchDataset = inParams.getGoodseedDataset("ds_search");
		if(searchDataset != null) {
			outParams.setGoodseedDataset("ds_master",
					getSqlManager().queryForGoodseedDataset(searchDataset, "commonCode.getCommonGroupCodeList"));
		} else {
			outParams.setGoodseedDataset("ds_master",
					getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getCommonGroupCodeList"));
		}
		if(outParams.getGoodseedDataset("ds_master").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 공통코드 정보를 조회한다.
	 *
	 * @author jay
	 * @since  2013. 4. 11.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getCommonCodeList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_detail",
				getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getCommonCodeList"));
		if(outParams.getGoodseedDataset("ds_detail").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 공통그룹코드 다국어 조회
	 * HTML 에서만 사용한다.
	 * @author jay
	 * @since  5. 3.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getCommonGroupCodeListMultilingualHTML(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);

		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");

		ArrayList<String> codeList = new ArrayList<String>();

		for(int row = 0; row < comParams.getRowCount(); row++) {
			comParams.setActiveRow(row);
			// 코드
			String cd = comParams.getColumnAsString(row, "COMM_CD");
			codeList.add(cd);

		}
		inParams.put("codeList", codeList);

		outParams.setGoodseedDataset("ds_master",
				getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getCommonGroupCodeListMultilingualHTML"));

		if(outParams.getGoodseedDataset("ds_master").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 공통코드 다국어 조회
	 * HTML 에서만 사용한다.
	 * @author jay
	 * @since  5. 3.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getCommonCodeListMultilingualHTML(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");

		ArrayList<String> codeList = new ArrayList<String>();

		for(int row = 0; row < comParams.getRowCount(); row++) {
			comParams.setActiveRow(row);
			// 코드
			String cd = comParams.getColumnAsString(row, "COMM_CD");
			codeList.add(cd);

		}
		inParams.put("codeList", codeList);

		outParams.setGoodseedDataset("ds_detail",
				getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getCommonCodeListMultilingualHTML"));
		if(outParams.getGoodseedDataset("ds_detail").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 메세지 번들 데이터를 MiPlatform gds_message 로 보낸다.
	 *
	 * @param inParams
	 * @throws IOException
	 */
	public Parameters getMessageList(Parameters inParams) throws IOException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("getMessageList called");
			LOG.debug("inParams : " + inParams);
		}
		Parameters params = inParams;
		GoodseedDataset message = new GoodseedHtmlDataset();
		message.addColumn("code", (short)1, 256);
		message.addColumn("message", (short)1, 256);

		String gLang = LocaleUtil.getUserLanguage(inParams);

		String path = null;
		String[] messageResourcePath = messageResourcePathList.getResourcePaths();

		HashMap msgMap = new HashMap();
		//message bundles(language) start
		for(int idx = 0; idx < messageResourcePath.length; idx++) {
			path = messageResourcePath[idx] + "_" + gLang + ".xml";
			if(LOG.isDebugEnabled()) {
				LOG.debug("noticeBundle file path : " + path);
			}

			File bundle = null;
			try {
				bundle = ResourceUtils.getFile(path);
			} catch(FileNotFoundException e) {
				LOG.debug("FileNotFoundException called.....", e);
				continue;
			}

			msgMap = parseMessageSourceXml(msgMap, bundle);

		}

		//message bundles(default) start
		for(int idx = 0; idx < messageResourcePath.length; idx++) {

			path = messageResourcePath[idx] + ".xml";

			if(LOG.isDebugEnabled()) {
				LOG.debug("noticeBundle file path : " + path);
			}

			File bundle = null;

			try {
				bundle = ResourceUtils.getFile(path);
			} catch(FileNotFoundException e) {
				LOG.debug("FileNotFoundException called.....", e);
				continue;
			}

			msgMap = parseMessageSourceXml(msgMap, bundle);
		}

		Set<Entry<String, String>> set = msgMap.entrySet();
		for(Entry<String, String> e : set) {
			int row = message.appendRow();
			message.setColumn(row, "code", e.getKey());
			message.setColumn(row, "message", e.getValue());
		}

		LOG.debug("message.getRowCount() : " + message.getRowCount());
		params.setGoodseedDataset("gds_message", message);

		return params;
	}

	/**
	 * 메세지 번들 데이터를 읽어서 HashMap으로 넘긴다.
	 *
	 * <br>
	 * @param msgMap
	 * @param bundle
	 * @return msgMap
	 * @throws IOException
	 * @author jay
	 * @since  7. 19.
	 */
	private HashMap parseMessageSourceXml(HashMap msgMap, File bundle) throws IOException {
		Document xml;
		BufferedInputStream bis = null;
		try {

			bis = new BufferedInputStream(new FileInputStream(bundle));

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setEntityResolver(new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});

			xml = builder.parse(bis);

			NodeList rootList = xml.getChildNodes();
			for(int i = 0; i < rootList.getLength(); i++) {
				Node rootNode = rootList.item(i);
				if(rootNode.hasChildNodes()) {
					NodeList firstNodeList = rootNode.getChildNodes();
					for(int j = 0; j < firstNodeList.getLength(); j++) {
						Node node = firstNodeList.item(j);
						if("entry".equals(node.getNodeName())) {
							NamedNodeMap attributes = node.getAttributes();

							if(msgMap.containsKey(attributes.item(0).getNodeValue())) {
								//LOG.debug("continued : " + attributes.item(0).getNodeValue());
								continue;
							}
							msgMap.put(attributes.item(0).getNodeValue(), node.getTextContent());
						}
					}
				}
			}
			//
		} catch(FileNotFoundException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} catch(ParserConfigurationException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} catch(SAXException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} catch(IOException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} finally {
			if(bis != null) {
				bis.close();
			}
		}
		return msgMap;
	}

	/**
	 * 공통그룹코드/공통코드를 저장한다.
	 * Miplatform 과 HTML 같이 사용한다.
	 * @author jay
	 * @since  2013. 4. 12.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveCommonCode(Parameters inParams) {

		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_master");
		GoodseedDataset detailDataset = inParams.getGoodseedDataset("ds_detail");

		if(GoodseedDataset != null || detailDataset != null) {
			// 공통코드그룹코드
			if(GoodseedDataset != null) {
				/* insert & update */
				for(int row = 0; row < GoodseedDataset.getRowCount(); row++) {
					GoodseedDataset.setActiveRow(row);

					// 신규
					if(GoodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
						// 중복 체크 조회

						Map<String, String> checkMap = new HashMap<String, String>();
						checkMap.put("COMM_CL_CD", GoodseedDataset.getColumnAsString(row, "COMM_CL_CD"));

						int cnt = 0;
						cnt = (Integer)getSqlManager().queryForObject(checkMap, "commonCode.getCommonGroupCodeRedundancyCheck");

						// 중복체크 데이터 결과값이 0 이상이면 중복으로 인식, 사용자 예외처리함.
						if(cnt > 0) {
							String strAddMsg =
									inParams.getVariableAsString("COMM_CL_CD") + " : '"
											+ GoodseedDataset.getColumnAsString(row, "COMM_CL_CD") + "'";
							throw new UserHandleException("MSG_COM_VAL_029", new String[]{strAddMsg});
						}

						getSqlManager().insert(GoodseedDataset, "commonCode.insertCommonGroupCode");

						//수정
					} else if(GoodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
						getSqlManager().update(GoodseedDataset, "commonCode.updateCommonGroupCode");
					}
				}

			}

			if(detailDataset != null) {
				/* insert & update */
				for(int row = 0; row < detailDataset.getRowCount(); row++) {
					detailDataset.setActiveRow(row);

					// 신규
					if(detailDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
						getSqlManager().insert(detailDataset, "commonCode.insertCommonCode");

					//수정
					} else if(detailDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
						getSqlManager().update(detailDataset, "commonCode.updateCommmonCode");
					}
				}
			}
			outParams.setMessage("MSG_COM_SUC_003");
		}
		return outParams;
	}

}
