package com.common.syscommon.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The class FrameOneDownloadUrlTag<br>
 * <br>
 * 다운로드 경로 생성<br>
 * 다운로드 컨트롤러로 연결하는 경로를 생성한다.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 17.
 *
 */
public class GoodseedDownloadUrlTag extends TagSupport {

	private static final long serialVersionUID = 5682923119845102628L;
	//private static final Log LOG = LogFactory.getLog(FrameOneDownloadUrlTag.class);
	
	//다운로드 컨트롤러 이후 물리적 파일경로 (physical path) -> 사용하지 않는 private 필드 주석처리
		//private String ppath;

		/*public void setPpath(String ppath) {
			this.ppath = ppath;
		}*/

	@Override
	public int doEndTag() throws JspException {

		/*
		try {

			//TODO:다운로드 컨트롤러가 생성되면 코드를 작성한다. 

			//		} catch(JspException ex) {
			//			logger.error(ex.getMessage(), ex);
			//			throw ex;
		} catch(RuntimeException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new JspTagException(ex.getMessage());
		}
		 */
		return EVAL_PAGE;
	}
}
