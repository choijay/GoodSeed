package goodseed.core.common.controller.html;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import goodseed.core.common.controller.BaseController;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.ParametersAndView;
import goodseed.core.common.model.factory.ParametersFactory;

/**
 * The class ExceptionController<br>
// * <br>
 * 공통 에러 처리 Controller <br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 4. 27.
 *
 */
@Controller
@RequestMapping("common/syscommon/exception")
public class ExceptionController extends BaseController {

	private static final Log LOG = LogFactory.getLog(ExceptionController.class);

	/**
	 * 공통 에러 페이지 뷰
	 * 
	 * @ahthor KimByungWook
	 * @since 4. 27.
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/exceptionInfo")
	public ParametersAndView exceptionInfo(HttpServletRequest request, HttpServletResponse response, Parameters inParams) {

		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setVariable("InnerReqYN", "Y");

		String referer = request.getHeader("Referer");
		String refererHost = null;

		if(referer != null) {
			try {
				URL url = new URL(referer);
				refererHost = url.getHost();
			} catch(MalformedURLException e) {
				LOG.error("exception", e);
				e.printStackTrace();
			}
			String serverName = request.getServerName();
			if(!serverName.equals(refererHost)) {
				outParams.setVariable("InnerReqYN", "N");
			}
		} else {
			outParams.setVariable("InnerReqYN", "N");
		}

		ParametersAndView pav = new ParametersAndView("common.syscommon.exceptionInfo");
		pav.setParameters(outParams);

		return pav;
	}

}
