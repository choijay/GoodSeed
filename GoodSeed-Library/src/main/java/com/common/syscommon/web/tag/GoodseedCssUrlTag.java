package com.common.syscommon.web.tag;


/**
 * The class FrameOneCssUrlTag<br>
 * <br>
 * 스타일시트(CSS) 파일 기본 경로 생성<br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 17.
 *
 */
public class GoodseedCssUrlTag extends GoodseedAbstractUrlTag {

	private static final long serialVersionUID = -1931354004244685212L;

	//private static final Log LOG = LogFactory.getLog(FrameOneCssUrlTag.class);

	/**
	 * 기본 생성자
	 */
	public GoodseedCssUrlTag() {
		super();
		this.type = GoodseedAbstractUrlTag.TYPE_CSS;
	}
}
