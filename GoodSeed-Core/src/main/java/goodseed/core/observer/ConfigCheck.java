package goodseed.core.observer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
*
* The class ConfigCheck<br>
* <br>
* Config가 리로드 되었을 때 업데이트가 필요한 클래스에서 사용하는 어노테이션
* <br>
* Copyright (c) 2014 GoodSeed<br>
* All rights reserved.<br>
* <br>
* This software is the proprietary information of GoodSeed<br>
* <br>
* @author jay
* @version 3.0
* @since  03. 20.
*
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigCheck{
	
}
