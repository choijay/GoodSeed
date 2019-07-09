package goodseed.core.common.controller.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import goodseed.core.common.controller.BaseController;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.utility.i18n.NoticeMessageUtil;

/**
 * The class MessageController<br>
 * <br>
 * 다국어  메시지 처리 Controller.
 * 설정된 다국어에 맞게 메시지 반환하는 역할을 한다.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 4. 27.
 *
 */
@Controller
@RequestMapping("goodseed/syscommon")
public class MessageController extends BaseController {

	/**
	 * 설정된 다국어에 맞게 메시지 반환.<br>
	 * <br>
	 * message argument는 클라이언트에서 javascript로 바인딩 해 준다.<br>
	 * inParams를 그대로 outParams로 보내야 할 경우 XSS 처리를 벗긴 Parametrers 객체를 보내야 ClassCastException이 발생하지 않는다.
	 * 
	 * @ahthor KimByungWook
	 * @since 4. 27.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param inParams
	 * @return
	 */
	@RequestMapping(value = "/searchMessage")
	public Parameters searchMessage(HttpServletRequest request, HttpServletResponse response, Parameters inParams) {
		inParams.setVariable("msgText",
				NoticeMessageUtil.getMessage(inParams.getVariableAsString("msgCode"), LocaleUtil.getUserLocale(inParams)));
		//inParams를 그대로 outParams로 보내야 할 경우 XSS 처리를 벗긴 Parametrers 객체를 보내야 ClassCastException이 발생하지 않는다.
		return inParams.getOriginalParameters();
	}
}
