/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.converter;

import org.apache.lucene.analysis.Analyzer;

import de.hotware.lucene.extension.bean.analyzer.AnalyzerProvider;
import de.hotware.lucene.extension.bean.annotations.BeanField;
import de.hotware.lucene.extension.util.Util;

/**
 * Constants and stuff
 * 
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
			DEFAULT_ANALYZER = analyzerProvider.getAnalyzer(null);
		} catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
