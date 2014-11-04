package de.hotware.lucene.extension.bean;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface BeanFields {

	/**
	 * <b>Attention: only specify one BeanField with store=true or the
	 *  behaviour of any method handling the BeanFields is not specified</b>
	 * @author Martin Braun
	 */
	public BeanField[] value();

}
