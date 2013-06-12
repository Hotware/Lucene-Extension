package de.hotware.lucene.extension.bean;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 * @author Martin Braun
 */
public enum LocaleAnalyzer {
	de_DE(new GermanAnalyzer(Version.LUCENE_43)),
	en_EN(new EnglishAnalyzer(Version.LUCENE_43)),
	DEFAULT(new StandardAnalyzer(Version.LUCENE_43));

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