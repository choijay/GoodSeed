/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * KongJungil		2010. 6. 16.		First Draft.
 */
package goodseed.core.utility.string;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import goodseed.core.exception.SystemException;

/**
 * 
 * The class StringUtil<br>
 * <br>
 * TODO String 문자열을 변환, 치환, 인코등 등의 기능을 제공하는 유틸 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2011 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * 
 * @author jay
 * @version 1.0
 *
 */
public class StringUtil {

	private static final Log LOG = LogFactory.getLog(StringUtil.class);

	/**
	 * 
	 * 입력받은 문자 "data"에서 입력받은 언더바"_"를 제거하고 Camel Hump 형태로 변환하여 리턴한다.<br>
	 * <br>
	 * 예) foo_barBar => FooBarBar<br>
	 * <br>
	 * <br>
	 * 
	 * @param data
	 * @param replaceThis
	 * @return
	 * @ahthor KimJiHye
	 */
	public static String removeAndHump(String key) {
		return removeAndHump(key, "_");
	}

	/**
	 * 
	 * 입력받은 문자 "data"에서 입력받은 "replaceThis"를 제거하고 Camel Hump 형태로 변환하여 리턴한다.<br>
	 * <br>
	 * 예) foo_barBar => FooBarBar<br>
	 * <br>
	 * <br>
	 * 
	 * @param data
	 * @param replaceThis
	 * @return
	 * @ahthor KimJiHye
	 */
	public static String removeAndHump(String data, String replaceThis) {
		String temp = null;
		StringBuilder out = new StringBuilder();
		temp = data;

		StringTokenizer st = new StringTokenizer(temp, replaceThis);

		while (st.hasMoreTokens()) {
			String element = (String) st.nextElement();
			out.append(capitalizeFirstLetter(element));
		} // while

		return out.toString();
	}

	/**
	 * 
	 * 첫번째 글자를 대문자로 변환하여 리턴한다.<br>
	 * <br>
	 * 예) fooBar => FooBar<br>
	 * <br>
	 * <br>
	 * 
	 * @param data
	 * @return
	 * @ahthor KimJiHye
	 */
	public static String capitalizeFirstLetter(String data) {
		String firstLetter = data.substring(0, 1).toUpperCase();
		String restLetters = data.substring(1);
		return firstLetter + restLetters;
	}

	/**
	 * 
	 * Camel 표기법으로 표기된 내용을 '_' 로 분리하여 리턴한다.<br>
	 * <br>
	 * 예) fooBar => foo_bar<br>
	 * <br>
	 * <br>
	 * 
	 * @param data
	 *            원본 문자열
	 * @return Camel 표기법으로 표기된 내용을 '_' 로 분리한 문자열
	 * @ahthor KimJiHye
	 */
	public static String removeCamelHump(String data) {
		return removeCamelHump(data, "_");
	}

	/**
	 * 
	 * Camel 표기법으로 표기된 내용을 입력한 문자 로 분리하여 표기한다.
	 * </p>
	 * <p>
	 * ex) removeCamelHump("fooBar", "_") <br/>
	 * <code>fooBar</code> --> <code>foo_bar</code>
	 * </p>
	 * 
	 * @param data
	 *            원본 문자열
	 * @param addThis
	 *            구분자
	 * @return Camel 표기법으로 표기된 내용을 입력한 문자 로 분리한 문자열
	 */
	public static String removeCamelHump(String data, String addThis) {

		String localData = data;
		String regexp = "[A-Z]";
		StringBuilder result = new StringBuilder();
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(localData);
		while (matcher.find()) {
			String left = localData.substring(0, matcher.start());
			String matchCharacter = localData.substring(matcher.start(), matcher.end());
			String right = localData.substring(matcher.end());
			localData = matchCharacter.toLowerCase() + right;
			result.append(left).append(addThis);
			matcher = pattern.matcher(localData);
		}
		result.append(localData);
		return result.toString();
	}

	/**
	 * 입력받은 문자열(src)의 시작부터 byteLength 까지 바이트 길이를 기준으로 자른 문자열을 리턴한다. 멀티바이트 문자가
	 * 포함된 문자열일 경우, byteLength까지 잘라도, 문자열을 구성하면서 byteLength를 넘어서는 경우가 있는데, 그 경우,
	 * 바로 전 문자까지만 구성하여 리턴하도록 처리되어 있으므로, 리턴되는 문자열의 바이트 길이는 byteLength보다 작아지게 된다.
	 * 현재는 UTF-8 형식의 문자열만 지원하고 있다.
	 * 
	 * @param src
	 *            입력 문자열
	 * @param byteLength
	 *            몇 바이트까지만 잘라서 리턴할 것인지
	 * @return byteLength까지 자른 문자열
	 */
	public static String substringByByteUTF8(String src, int byteLength) {

		String ret = null;
		String encoding = "UTF-8";
		if (StringUtils.isBlank(src)) {
			return "";
		}
		if (byteLength < 0) {
			throw new SystemException("byteLength must be positive");
		}
		byte[] srcBytes = null;
		int srcByteLength = 0;
		try {
			srcBytes = src.getBytes(encoding);
			srcByteLength = srcBytes.length;

			// 자를 필요가 없을 경우 원본 문자열 리턴
			if (srcByteLength <= byteLength) {
				return src;
			}

			// 잘라야 할 필요가 있는 (srcByteLength > byteLength) 경우
			/*
			 * byte로 잘랐을 때, 마지막 byte의 다음 byte가 다음 문자의 시작 byte이면 정확히 문자 단위로 잘린
			 * 것이라서 이상이 없지만, 멀티바이트 문자일 경우, 마지막 byte의 다음 byte가 다음 문자의 시작 byte가 아닐
			 * 경우, 그 byte에 이어서 다른 byte를 더 붙여서 문자를 완결짓기 때문에, 최초 제한하려고 했던 byte보다 더
			 * 긴 byte 길이를 갖는 결과를 리턴하게 되어 DB 처리시 오류를 발생시킬 수 있다. 그렇기 때문에 byte로 잘랐을
			 * 때 문자단위로 끊어지지 않는다면, 바로 전 문자까지만 잘라서 리턴해 주어야 한다. (결과적으로, 최초 제한하려고 했던
			 * byte 보다 작은 길이의 byte로 된 문자열을 리턴하게 됨)
			 * 
			 * UTF-8 설계 원칙 1바이트로 표시된 문자의 최상위 비트는 항상 0이다. 2바이트 이상으로 표시된 문자의 경우, 첫
			 * 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로
			 * 시작하고, 3바이트는 1110으로 시작한다. 첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
			 */
			// 얻고자 하는 마지막 바이트의 배열 인덱스
			int lastByteIndex = byteLength - 1;
			// 얻고자 하는 마지막 바이트의 바로 다음 바이트의 배열 인덱스
			int lastByteNextIndex = lastByteIndex + 1;
			// 얻고자 하는 마지막 바이트의 바로 다음 바이트
			byte lastByteNextByte = srcBytes[lastByteNextIndex];

			// lastByteNextByte가 멀티바이트 문자의 시작바이트가 아닌 경우 - 멀티바이트 문자의 중간을 자른
			// 형국이므로, 바로 전 시작바이트 앞을 기점으로 끊도록 lastByteIndex의 값을 보정한다.
			String lastByteNextBinaryString = StringUtil.toBinary(new byte[] { lastByteNextByte });
			// 마지막 이후 바이트 이진수 변환
			if (lastByteNextBinaryString.startsWith("10")) {
				int offset = 0;
				for (int i = lastByteIndex; i >= 0; i--) {
					offset--;
					String bin = StringUtil.toBinary(new byte[] { srcBytes[i] });
					if (!bin.startsWith("10")) {
						break;
					}
				}
				lastByteIndex += offset;
				if (LOG.isDebugEnabled()) {
					LOG.debug(" cutting offset for multibyte charecter processing : " + offset);
				}
			}
			// 그 외의 경우 마지막에 자른 바이트가 문자 단위로 잘렸으므로 별다른 처리를 하지 않는다.

			ret = new String(srcBytes, 0, (lastByteIndex + 1), encoding);

			if (LOG.isDebugEnabled()) {
				LOG.debug(" src length : " + src.length());
				LOG.debug(" src byte length : " + srcByteLength);
				LOG.debug(" lastByteIndex : " + lastByteIndex);
				LOG.debug(" lastByteNextBinaryString : " + lastByteNextBinaryString);
				LOG.debug(" ret length : " + ret.length());
				LOG.debug(" ret byte length : " + ret.getBytes(encoding).length);
			}

		} catch (Exception e) {
			LOG.error("exception", e);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 바이트 배열을 입력 받아 이진수 문자열로 리턴한다.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toBinary(byte[] bytes) {
		final int BYTE_SIZE = Byte.SIZE; // 8
		StringBuilder sb = new StringBuilder(bytes.length * BYTE_SIZE);
		for (int i = 0; i < BYTE_SIZE * bytes.length; i++) {
			sb.append(((bytes[i / BYTE_SIZE] << (i % BYTE_SIZE)) & 0x80) == 0 ? '0' : '1');
		}
		return sb.toString();
	}

	/**
	 * StringUtils.defaultString()과 같으나 Object 인수를 받을 수 있도록 처리한 메서드
	 * 
	 * @param obj
	 * @return
	 */
	public static String defaultString(Object obj) {
		return defaultString(obj, "");
	}

	/**
	 * StringUtils.defaultString()과 같으나 Object 인수를 받을 수 있도록 처리한 메서드
	 * 
	 * @param obj
	 * @return
	 */
	public static String defaultString(Object obj, String defaultStr) {
		return obj != null ? obj.toString() : defaultStr;
	}

	/**
	 * 인수로 받는 객체를 JSON string으로 리턴한다.
	 * 
	 * @param obj
	 * @return JSON string
	 */
	public static String getJSONString(Object obj) {
		String ret = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			ret = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			LOG.error("exception", e);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 원본문자열을 URL encode (UTF-8) 하여 리턴하는 메서드<br>
	 * <br>
	 * 
	 * @param src
	 * @return
	 */
	public static String encodeUrlUtf8(String src) {
		String ret = null;
		try {
			ret = URLEncoder.encode(StringUtils.defaultString(src), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("exception", e);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 원본문자열을 URL decode (UTF-8) 하여 리턴하는 메서드<br>
	 * <br>
	 * 
	 * @param src
	 * @return
	 */
	public static String decodeUrlUtf8(String src) {
		String ret = null;
		try {
			ret = URLDecoder.decode(StringUtils.defaultString(src), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("exception", e);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 
	 * 입력받은 List<HashMap>에서 각각의 Map객체의 Key들을 대문자로 변환하여 리턴한다.<br>
	 * <br>
	 * 
	 * @param inList
	 * @return outList 모든 키가 대문자로 변환된 Map을 담는 List객체
	 * @ahthor KimJiHye
	 */
	public static List<HashMap> getUpperKeyList(List<HashMap> inList) {
		List<HashMap> outList = new ArrayList<HashMap>();
		for (HashMap m : inList) {
			Object[] hashKey = m.keySet().toArray();
			HashMap h = new HashMap();
			for (Object o : hashKey) {
				String s = (String) o;
				String upperS = s.toUpperCase();
				h.put(upperS, m.get(s));
			}
			outList.add(h);
		}

		return outList;
	}

	/**
	 * 입력 문자열의 왼쪽에 pad되어 있는 character를 제거
	 * 
	 * @param str
	 *            입력문자열
	 * @param pad
	 *            제거할 char (space or 0) 1자리
	 * @return
	 */
	public static String removeLeftPad(String str, String pad) {

		if (StringUtils.isBlank(str) || StringUtils.isBlank(pad))
			return str;

		char padChar = pad.charAt(0);
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != padChar) {
				return str.substring(i);
			}
		}

		return "";
	}

	/**
	 * 입력 문자열의 오른쪽에 pad되어 있는 character를 제거
	 * 
	 * @param str
	 *            입력문자열
	 * @param pad
	 *            제거할 char (space or 0)
	 * @return
	 */
	public static String removeRightPad(String str, String pad) {

		if (StringUtils.isBlank(str) || StringUtils.isBlank(pad))
			return str;

		char padChar = pad.charAt(0);
		char[] chars = str.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			if (chars[i] != padChar) {
				return str.substring(0, i + 1);
			}
		}

		return "";
	}

	/**
	 * 전각문자열 만들어 리턴(1byte 반각문자열을 2byte 전각문자열로 변환)
	 * @param src
	 * @return
	 */
	public static String toFullChar(String src) {
		// 입력된 스트링이 null 이면 null 을 리턴
		if (src == null)
			return null;
		// 변환된 문자들을 쌓아놓을 StringBuffer 를 마련한다
		StringBuffer strBuf = new StringBuffer();
		char c = 0;
		int nSrcLength = src.length();
		for (int i = 0; i < nSrcLength; i++) {
			c = src.charAt(i);
			// 영문이거나 특수 문자 일경우.
			if (c >= 0x21 && c <= 0x7e) {
				c += 0xfee0;
			}
			// 공백일경우
			else if (c == 0x20) {
				c = 0x3000;
			}
			// 문자열 버퍼에 변환된 문자를 쌓는다
			strBuf.append(c);
		}
		return strBuf.toString();
	}

}
