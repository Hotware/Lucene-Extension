package com.github.hotware.lucene.extension.bean.hsearch.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface DtoOverEntity {
	
	public Class<?> entityClass();

}
