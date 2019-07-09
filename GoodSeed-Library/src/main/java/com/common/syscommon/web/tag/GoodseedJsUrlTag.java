package com.common.syscommon.web.tag;


/**
 * The class FrameOneJsUrlTag<br>
 * <br>
 * 자바스크립트(JS) 파일 기본 경로 생성<br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 17.
 *
 */
public class GoodseedJsUrlTag extends GoodseedAbstractUrlTag {

	private static final long serialVersionUID = -4662903280681935757L;

	//private static final Log LOG = LogFactory.getLog(FrameOneJsUrlTag.class);

	/**
	 * 기본 생성자
	 */
	public GoodseedJsUrlTag() {
		super();
		this.type = GoodseedAbstractUrlTag.TYPE_JS;
	}
}
