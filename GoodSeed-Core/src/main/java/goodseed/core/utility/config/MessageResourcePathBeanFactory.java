package goodseed.core.utility.config;

import org.springframework.beans.factory.FactoryBean;

/**
 * The class MessageResourcePathBeanFactory<br>
 * 
 * 메세지소스의 경로관련 Class
 * <br>
 * @author jay
 *
 */
public class MessageResourcePathBeanFactory implements FactoryBean<String[]> {

	private String[] resourcePaths;

	/**
	 * Getter<br>
	 * MessageResource의 경로를 얻는다.
	 * <br>
	 * @return String[]
	 */
	public String[] getResourcePaths() {
		return resourcePaths;
	}

	/**
	 * Setter<br>
	 * MessageResource의 경로를 지정한다.
	 * <br>
	 * @param resourcePaths
	 */
	public void setResourcePaths(String[] resourcePaths) {
		this.resourcePaths = resourcePaths;
	}

	@Override
	public String[] getObject() {
		return resourcePaths;
	}

	@Override
	public Class<String[]> getObjectType() {
		return String[].class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
