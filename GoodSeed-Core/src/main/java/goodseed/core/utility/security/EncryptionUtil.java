/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * 		2009. 10. 15.		First Draft.
 * Jungil Kong      2010.  3. 30.       이름 변경
 */
package goodseed.core.utility.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.exception.SystemException;

/**
 * The class EncryptUtils<br/>
 * 1~16 자리의 문자를 SEED 알고리즘을 사용하여 암/복호화 하는 Class<br>
 * <br>
 * @author jay
 * @version 1.0
 * 
 */
public class EncryptionUtil {

	private static final Log LOG = LogFactory.getLog(EncryptionUtil.class);

	public static final String ISO8859_1 = "iso8859-1";

	//    /** Round keys for encryption or decryption */
	//    private static int pdwRoundKey[] = new int[32];
	//    /** User secret key */
	//    private static final byte PB_USER_KEY[] = { (byte) 0x01, (byte) 0xFF, (byte) 0x13, (byte) 0x00,
	//            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x0A,
	//            (byte) 0x00, (byte) 0x09, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	//
	//    /** encrypted data(output data) */
	//    private static byte pbCipher[] = new byte[16];
	//    /** plain data(output data) */
	//    private static byte pbPlain[] = new byte[16];

	//    /**
	//     * 암호화.<br/>
	//     * SEED 알고리즘(128bit)을 사용한 암호화.
	//     * 
	//     * @param data
	//     * @return string 32byte / cipher text
	//     */
	//    public static String seedEncrypt(String data) {
	//        SEED.seedRoundKey(pdwRoundKey, PB_USER_KEY);
	//        byte pbData[] = encryptToBytes(data);
	//        SEED.seedEncrypt(pbData, pdwRoundKey, pbCipher);
	//
	//        /* 암호화 한 data 를 string 으로 변환 */
	//        StringBuffer buffer = new StringBuffer();
	//        for (int i = 0; i < pbCipher.length; i++) {
	//            String hexString = Integer.toHexString(0xff & pbCipher[i]);
	//            if (hexString.length() == 1) {
	//                hexString = "0" + hexString;
	//            }
	//            buffer.append(hexString);
	//        }
	//
	//        return buffer.toString();
	//    }
	//
	//    /**
	//     * 복호화.<br/>
	//     * SEED 알고리즘(128bit)으로 암호화안 내용을 복호화 한다.
	//     * 
	//     * @param encryptedData
	//     * @return string / plain text
	//     */
	//    public static String seedDecrypt(String encryptedData) {
	//        SEED.seedRoundKey(pdwRoundKey, PB_USER_KEY);
	//        byte pbData[] = decryptToBytes(encryptedData);
	//        SEED.seedDecrypt(pbData, pdwRoundKey, pbPlain);
	//        return new String(pbPlain);
	//    }
	//
	//    /**
	//     * String 을 byte[] 로 변환.(암호화시)
	//     * 
	//     * @param key
	//     * @return
	//     */
	//    private static byte[] encryptToBytes(String data) {
	//        int lengthCheck = data.length() % 8;
	//        int keyLength = data.length();
	//        int delta = 16 - keyLength;
	//        if (lengthCheck != 0 || keyLength == 8) {
	//            for (int index = 0; index < delta; index++) {
	//                data += " ";
	//            }
	//        }
	//        return data.getBytes();
	//    }
	//
	//    /**
	//     * String 을 byte[] 로 변환(복호화시)
	//     * 
	//     * @param s
	//     * @return
	//     */
	//    private static byte[] decryptToBytes(String encryptData) {
	//        int length = encryptData.length();
	//        byte[] b = new byte[length / 2];
	//        for (int i = 0; i < length / 2; i++) {
	//            String str = encryptData.substring(i * 2, (i * 2) + 2);
	//            b[i] = (byte) ((Byte.parseByte(str.substring(0, 1), 16) << 4) | Byte.parseByte(str
	//                    .substring(1, 2), 16));
	//        }
	//        return b;
	//    }

	/**
	 * 입력 받은 바이트 데이터를 암호화 하여 리턴한다.<br>
	 *<br>
	 * @param inData => TODO : @param argument "inData" is not a parameter name. String 평문
	 * @param szKey   byte[] key
	 * @return byte[] 암호화 된 데이터
	 */
	public static byte[] encrypt(byte[] sbuffer, byte[] szKey) {
		SeedCipher cipher = new SeedCipher();
		return cipher.encrypt(sbuffer, szKey);
	}

	/**
	 * 입력 받은 문자열을 암호화 하여 리턴한다.<br>
	 * 시스템 기본 Charset 사용<br>
	 *<br>
	 * @param inData  String 평문
	 * @param szKey  byte[] key
	 * @return byte[] 암호화 된 데이터
	 */
	public static byte[] encrypt(String inData, byte[] szKey) {
		return encrypt(inData.getBytes(), szKey);
	}

	/**
	 * 문자열을 특정 Charset으로 변환하여 암호화 하여 리턴한다.<br>
	 *<br>
	 * @param inData String 평문
	 * @param szKey byte[] key
	 * @param charset String String을 byte 데이터로 변환할때 사용할 charset
	 * @return byte[] 암호화 된 데이터
	 */
	public static byte[] encrypt(String inData, byte[] szKey, String charset) throws UnsupportedEncodingException {

		return encrypt(inData.getBytes(charset), szKey);
	}

	/**
	 * 암호화 된 바이트 데이터를 받아서 복호화한다.<br>
	 *<br>
	 * @param encryptBytes byte[] 암호화 된 바이트 데이터
	 * @param szKey  byte[] key
	 * @return byte[] 복호화 된 바이트 데이터
	 */
	public static byte[] decrypt(byte[] encryptBytes, byte[] szKey) {
		SeedCipher cipher = new SeedCipher();
		return cipher.decrypt(encryptBytes, szKey);
	}

	/**
	 * 데이터를 받아서 복호화하여 문자열로 반환한다.<br>
	 *<br>
	 * @param encryptBytes byte[] 암호화 된 바이트 데이터
	 * @param szKey byte[] key
	 * @return byte[] 복호화 된 바이트 데이터
	 */
	public static String decryptAsString(byte[] encryptBytes, byte[] szKey) {
		return new String(decrypt(encryptBytes, szKey));
	}

	/**
	 * 암호화 된 바이트 데이터를 받아서 복호화하여 지정한 charset으로 문자열로 반환한다.<br>
	 *<br>
	 * @param encryptBytes byte[] 암호화 된 바이트 데이터
	 * @param szKey  byte[] key
	 * @param String => TODO : @param argument "String" is not a parameter name.
	 *            charset 복호화 된 바이트 데이터를 문자열로 변환할 때 사용할 Charset
	 * @return byte[] 복호화 된 바이트 데이터
	 */
	public static String decryptAsString(byte[] encryptBytes, byte[] szKey, String charset) throws UnsupportedEncodingException {

		return new String(decrypt(encryptBytes, szKey), charset);
	}

	/**
	 * 임의의 길이의 문자열을 받아 MD5암호화 알고리즘을 통해 16진수 암호화된 값을 반환한다.<br>
	 *<br>
	 * @param str 암호화할 임의의 길이의 문자열
	 * @return String	고정길이 암호화문자열
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static String digestMD5(String str){
		byte[] defaultBytes = str.getBytes();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte[] messageDigest = algorithm.digest();

			StringBuilder hexString = new StringBuilder();

			for(int i = 0; i < messageDigest.length; i++) {
				hexString.append(String.format("%02X", 0xFF & messageDigest[i]));
			}

			return hexString.toString();
		} catch(NoSuchAlgorithmException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * 입력 받은 바이트 데이터 SHA256으로 변환한다.<br>
	 * <br>
	 * TODO : @param argument is not a parameter name.
	 * @param inData  String 평문
	 * @param szKey byte[] key
	 * @return byte[] 암호화 된 데이터
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] digestSHA256(byte[] sbuffer){
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			return digest.digest(sbuffer);
		} catch(NoSuchAlgorithmException e) {
			LOG.error("Exception",e);
			return null;
		}
	}

	/**
	 * 입력 받은 바이트 데이터 SHA256으로 변환하여 바이너리 헥사 코드 문자열을 변환한다.<br>
	 * <br>
	 * @param data String 평문
	 * @return String 암호화 된 데이터의 헥사 코드 문자열
	 */
	public static String digestSHA256(String data) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			byte[] bin = digest.digest(data.getBytes("ISO8859-1"));
			String mdData = "";
			for(byte b : bin) {
				String t = Integer.toHexString(b);
				if(t.length() < 2) {
					mdData += "0" + t;
				} else {
					mdData += t.substring(t.length() - 2);
				}
			}
			return mdData;
		} catch(NoSuchAlgorithmException e) {
			LOG.error("Exception", e);
			return null;
		} catch(UnsupportedEncodingException e) {
			LOG.error("Exception", e);
			return null;
		}
	}

	/**
	 * 입력 받은 바이트 데이터 SHA256으로 변환하여, base64 코드화 문자열을 반환한다.<br>
	 * <br>
	 * @param data String 평문
	 * @return byte[] 암호화 된 데이터의 base64 코드 문자열
	 */
	public static String digestSHA256Base64(String data) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			byte[] bin = digest.digest(data.getBytes("ISO8859-1"));
			return Base64.encodeBase64String(bin);
		} catch(NoSuchAlgorithmException e) {
			LOG.error("Exception", e);
			return null;
		} catch(UnsupportedEncodingException e) {
			LOG.error("Exception", e);
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(digestSHA256("1111"));
	}
}
