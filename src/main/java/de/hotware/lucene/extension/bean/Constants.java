package de.hotware.lucene.extension.bean;

import de.hotware.lucene.extension.bean.BeanField.AnalyzerWrapper;

public class Constants {
	
	public static final String DEFAULT_NAME = (String) Util
			.getDefaultValueForAnnotationMethod(BeanField.class, "name");
	public static final AnalyzerWrapper DEFAULT_ANALYZER = (AnalyzerWrapper) Util
			.getDefaultValueForAnnotationMethod(BeanField.class, "analyzer");

}
