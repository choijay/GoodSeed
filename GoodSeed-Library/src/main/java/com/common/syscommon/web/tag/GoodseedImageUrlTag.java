package com.common.syscommon.web.tag;


/**
 * The class FrameOneImageUrlTag<br>
 * <br>
 * 이미지파일 기본 경로 생성<br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 17.
 *
 */
public class GoodseedImageUrlTag extends GoodseedAbstractUrlTag {

	private static final long serialVersionUID = 5682923119845102628L;

	//private static final Log LOG = LogFactory.getLog(FrameOneImageUrlTag.class);

	/**
	 * 기본 생성자
	 */
	public GoodseedImageUrlTag() {
		super();
		this.type = GoodseedAbstractUrlTag.TYPE_IMAGE;
	}

}
