package de.hotware.lucene.extension.bean;

import org.apache.lucene.analysis.Analyzer;

import de.hotware.lucene.extension.bean.analyzer.AnalyzerProvider;

/**
 * @author Martin Braun
 */
public class Constants {

	private Constants() {
		throw new AssertionError("can't touch this!");
	}

	public static final String DEFAULT_NAME = (String) Util
			.getDefaultValueForAnnotationMethod(BeanField.class, "name");
	public static final Analyzer DEFAULT_ANALYZER;
	static {
		try {
			AnalyzerProvider analyzerProvider = (AnalyzerProvider) ((Class<?>)Util
				.getDefaultValueForAnnotationMethod(BeanField.class,
						"analyzerProvider")).newInstance();
			DEFAULT_ANALYZER = analyzerProvider.getAnalyzer();
		} catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
