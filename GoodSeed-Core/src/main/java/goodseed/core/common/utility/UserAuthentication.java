/*
 * Copyright (c) 2011 CJ OliveNetworks All rights reserved.
 * 
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.common.utility;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * The class UserAuthentication<br>
 * smtp mail 전송시 사용자의 인증정보를 위한 class
 * @author jay
 *
 */
public class UserAuthentication extends Authenticator {

	private PasswordAuthentication pa;
	
	/**
	 * smtp mail전송시 사용자의 id, password 인증정보 처리
	 * @param id
	 * @param password
	 */
	public UserAuthentication(String id, String password) {
		pa = new PasswordAuthentication(id, password);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return pa;
	}
}
