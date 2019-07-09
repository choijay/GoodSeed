package goodseed.core.common.handler;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResponseInScopeFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(ResponseInScopeFilter.class);

	// not the most elegant, but our spring commiter friends suggested this way.
	private ThreadLocal<HttpServletResponse> responses = new ThreadLocal<HttpServletResponse>();

	public void init(FilterConfig filterConfig) throws ServletException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Init ResponseInScopeFilter");
		}
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
			ServletException {
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		responses.set(response);
		chain.doFilter(servletRequest, servletResponse);
		responses.remove();
	}

	/** Only to be used by the BeanFactory */
	public HttpServletResponse getHttpServletResponse() {
		return responses.get();
	}

	public void destroy() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("DESTROY");
		}
	}
}
