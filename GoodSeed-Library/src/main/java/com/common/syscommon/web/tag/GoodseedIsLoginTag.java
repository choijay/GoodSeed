package com.common.syscommon.web.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;
import goodseed.core.utility.authority.AuthorityRule;

/**
 * The class FrameOneIsLoginTag<br>
 * <br>
 * Tag의 body를 로그인한 경우에만 실행되는 영역으로 처리한다<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 17.
 *
 */
public class GoodseedIsLoginTag extends ConditionalTagSupport {

	private static final long serialVersionUID = 6905146927962349611L;

	private AuthorityRule authorityRule;

	public GoodseedIsLoginTag() {
		authorityRule = ((AuthorityRule)GoodseedContextLoaderAdapter.getBean("authorityRule"));
		init();
	}

	private void init() {
		//IfTag 에는 test = null; 코드가 있었음
	}

	public void release() {
		super.release();
		init();
	}

	/**
	 * 	condition()에서 조건비교를 하여 boolean을 리턴하면 tag의 body가 if문으로 처리된다.
	 */
	protected boolean condition() throws JspTagException {
		return authorityRule.isLogin(pageContext.getRequest());
	}

}
