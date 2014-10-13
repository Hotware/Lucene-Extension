package de.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import de.hotware.lucene.extension.bean.LuceneVersion;

/**
 * some Basic AnalyzerProviders implemented for your convenience
 * 
 * @author Martin Braun
 */
public class StockAnalyzerProvider {

	private StockAnalyzerProvider() {
		throw new AssertionError("can't touch this!");
	}

	private static abstract class AnalyzerProviderTemplate implements
			AnalyzerProvider {

		private final Analyzer analyzer;

		protected AnalyzerProviderTemplate(Analyzer analyzer) {
			this.analyzer = analyzer;
		}

		@Override
		public Analyzer getAnalyzer() {
			return this.analyzer;
		}

	}

	public static class KeyWordAnalyzerProvider extends
			AnalyzerProviderTemplate {

		public KeyWordAnalyzerProvider() {
			super(new KeywordAnalyzer());
		}

	}

	public static class GermanAnalyzerProvider extends AnalyzerProviderTemplate {

		public GermanAnalyzerProvider() {
			super(new GermanAnalyzer(LuceneVersion.VERSION));
		}

	}

	public static class EnglishAnalyzerProvider extends
			AnalyzerProviderTemplate {

		public EnglishAnalyzerProvider() {
			super(new EnglishAnalyzer(LuceneVersion.VERSION));
		}

	}

	public static class StandardAnalyzerProvider extends
			AnalyzerProviderTemplate {

		public StandardAnalyzerProvider() {
			super(new StandardAnalyzer(LuceneVersion.VERSION));
		}

	}

}
