/*
 * Copyright (c) 2011 CJ OliveNetworks All rights reserved.
 * 
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.utility.process;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.exception.SystemException;

/**
 *	The class ExecuteUtil<br>
 *	<br>	
 *	Java runtime을 사용하여 커맨드라인 명령을 수행하기 위해 만든 유틸 클래스<br>
 *	<br>	
 * 
 * @author jay
 */
public class ExecuteUtil {

	private static final Log LOG = LogFactory.getLog(ExecuteUtil.class);

	/**
	 * 명령을 shell로 실행하고, output을 반환한다.<br>
	 *<br>
	 * @param cmd 실행할 명령
	 * @param timeout 대기할 최대 시간. 단위 ms
	 * @return String stdout Runtime.exec() 수행시의 표준출력
	 */
	public static String execute(String cmd, long timeout) {
		return execute(cmd, timeout, null);
	}

	/**
	 * 명령을 shell로 실행하고, output을 반환한다.<br>
	 * <br>
	 * @param cmd 실행할 명령
	 * @param timeout 대기할 최대 시간. 단위 ms
	 * @param workingPath Runtime.exec()의 인수로 들어가는 workingPath
	 * @return stdout Runtime.exec() 수행시의 표준출력
	 */
	public static String execute(String cmd, long timeout, String workingPath) {
		//타임아웃 처리
		new Thread(new Waker(Thread.currentThread(), timeout)).start();
		BufferedInputStream bis = null;
		Process process = null;
		try {
			if(workingPath == null) {
				process = Runtime.getRuntime().exec(cmd);
			} else {
				process = Runtime.getRuntime().exec(cmd, null, new File(workingPath));
			}
			InputStream stdin = process.getInputStream();
			bis = new BufferedInputStream(stdin);
			byte[] buff = new byte[1024];
			//			int exitVal = process.waitFor();
			//동기화 처리
			process.waitFor();
			StringBuilder sbout = new StringBuilder();
			while(bis.read(buff) != -1) {
				sbout.append(new String(buff, 0, bis.read(buff)));
			}
			return sbout.toString();
		} catch(IOException e) {
			throw new SystemException(e);
		} catch(InterruptedException e) {
			throw new SystemException(e);
		} finally {
			if(bis != null) {
				try {
					bis.close();
				} catch(IOException e) {
					LOG.error("exception", e);
					e.printStackTrace();
				}
			}
			if(process != null) {
				process.destroy();
			}
		}
	}

	/**
	 *	커맨드라인 명령 수행시 Timeout을 주기 위해 만든 클래스<br>
	 *	<br>
	 * @author jay
	 */
	static class Waker implements Runnable {

		private Thread t;
		private long timeout;

		/**
		 * 생성자<br>
		 * <br>
		 * @param toWake interrupt할 Thread
		 * @param timeout 설정한 타임아웃
		 */
		Waker(Thread toWake, long timeout) {
			this.t = toWake;
			this.timeout = timeout;
		}

		/**
		 * 타임아웃만큼 기다린 후 t 스레드를 interrupt한다.<br>
		 */
		public void run() {
			synchronized(this) {
				try {
					this.wait(timeout);
				} catch(InterruptedException e) {
					LOG.error("exception", e);
					e.printStackTrace();
				}
			}
			t.interrupt();
		}
	}
}
