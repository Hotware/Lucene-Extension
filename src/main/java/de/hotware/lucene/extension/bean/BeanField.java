package de.hotware.lucene.extension.bean;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import de.hotware.lucene.extension.bean.analyzer.AnalyzerProvider;
import de.hotware.lucene.extension.bean.analyzer.StockAnalyzerProvider;
import de.hotware.lucene.extension.bean.type.StockType;
import de.hotware.lucene.extension.bean.type.Type;

/**
 * Utility class used for annotation of Beans that should be stored into lucene
 * Use this class in addition with a BeanConverter Implementation
 * 
 * @author Martin Braun
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface BeanField {

	public String name() default "#DEFAULT";

	public boolean index() default false;

	public boolean store() default true;
	
	public boolean tokenized() default true;

	public Class<? extends Type> type() default StockType.SerializeType.class;

	public Class<? extends AnalyzerProvider> analyzerProvider() default StockAnalyzerProvider.StandardAnalyzerProvider.class;

}
