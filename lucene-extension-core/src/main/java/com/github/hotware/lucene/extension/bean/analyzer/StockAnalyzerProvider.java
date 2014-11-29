/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.github.hotware.lucene.extension.bean.field.FieldInformation;

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
		public Analyzer getAnalyzer(FieldInformation fieldInformation) {
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
			super(new GermanAnalyzer());
		}

	}

	public static class EnglishAnalyzerProvider extends
			AnalyzerProviderTemplate {

		public EnglishAnalyzerProvider() {
			super(new EnglishAnalyzer());
		}

	}

	public static class StandardAnalyzerProvider extends
			AnalyzerProviderTemplate {

		public StandardAnalyzerProvider() {
			super(new StandardAnalyzer());
		}

	}

}
