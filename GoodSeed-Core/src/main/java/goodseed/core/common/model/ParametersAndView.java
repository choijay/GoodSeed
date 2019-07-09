package goodseed.core.common.model;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * The class ParametersAndView<br>
 * <br>
 * Parameters 객체를 포함한 ModelAndView<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 4. 27.
 *
 */
public class ParametersAndView extends ModelAndView {

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 */
	public ParametersAndView() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 * @param viewName
	 * @param model
	 */
	public ParametersAndView(String viewName, Map<String, ?> model) {
		super(viewName, model);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 * @param viewName
	 * @param modelName
	 * @param modelObject
	 */
	public ParametersAndView(String viewName, String modelName, Object modelObject) {
		super(viewName, modelName, modelObject);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 * @param viewName
	 */
	public ParametersAndView(String viewName) {
		super(viewName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 * @param view
	 * @param model
	 */
	public ParametersAndView(View view, Map<String, ?> model) {
		super(view, model);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 * @param view
	 * @param modelName
	 * @param modelObject
	 */
	public ParametersAndView(View view, String modelName, Object modelObject) {
		super(view, modelName, modelObject);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 27.
	 *
	 * @param view
	 */
	public ParametersAndView(View view) {
		super(view);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Parameters 객체 를 설정 한다.
	 * HtmlParameters 객체를 생성 하고 난 뒤에 Paramters 객체에 필요 한
	 * 데이타를 모두 담은 뒤에  ParametersAndView 에  세팅해야한다.
	 * 
	 * @ahthor KimByungWook
	 * @since 4. 27.
	 *
	 * @param outParams
	 */
	public void setParameters(Parameters outParams) {
		super.addObject(outParams);
	}

}
