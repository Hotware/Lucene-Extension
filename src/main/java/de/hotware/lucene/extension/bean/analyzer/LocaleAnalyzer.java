package de.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import de.hotware.lucene.extension.bean.LuceneVersion;

/**
 * @author Martin Braun
 */
public enum LocaleAnalyzer {
	de_DE(new GermanAnalyzer(LuceneVersion.VERSION)),
	en_EN(new EnglishAnalyzer(LuceneVersion.VERSION)),
	DEFAULT(new StandardAnalyzer(LuceneVersion.VERSION));

	private final Analyzer analyzer;

	private LocaleAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public static Analyzer getAnalyzer(String locale) {
		LocaleAnalyzer localeAnalyzer = LocaleAnalyzer.valueOf(locale);
		if(localeAnalyzer == null) {
			localeAnalyzer = DEFAULT;
		}
		return localeAnalyzer.analyzer;
	}

}