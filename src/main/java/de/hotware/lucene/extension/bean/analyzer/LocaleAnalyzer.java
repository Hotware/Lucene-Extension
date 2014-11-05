/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import de.hotware.lucene.extension.bean.LuceneVersion;

/**
 * Locale dependent Analyzer helper class. So far only de_DE and en_EN are
 * supported. <br />
 * <br />
 * In order to use this class, you should provide your own
 * {@link AnalyzerProvider}
 * 
 * @author Martin Braun
 */
public enum LocaleAnalyzer {
	de_DE(new GermanAnalyzer(LuceneVersion.VERSION)), en_EN(
			new EnglishAnalyzer(LuceneVersion.VERSION)), DEFAULT(
			new StandardAnalyzer(LuceneVersion.VERSION));

	private final Analyzer analyzer;

	private LocaleAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public static Analyzer getAnalyzer(String locale) {
		LocaleAnalyzer localeAnalyzer = LocaleAnalyzer.valueOf(locale);
		if (localeAnalyzer == null) {
			localeAnalyzer = DEFAULT;
		}
		return localeAnalyzer.analyzer;
	}

}