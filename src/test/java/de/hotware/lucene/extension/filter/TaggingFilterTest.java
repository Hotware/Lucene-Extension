/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.filter;

import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;

import de.hotware.lucene.extension.filter.TaggingFilter.IndexFormatProvider;
import de.hotware.lucene.extension.util.Tokenize;
import junit.framework.TestCase;

public class TaggingFilterTest extends TestCase {

	public static class SimpleStartEndTaggingAnalyzer extends Analyzer {

		@Override
		protected TokenStreamComponents createComponents(String fieldName,
				Reader reader) {
			// we use a tokenizer that doesn't remove dots,
			// hyphens or whatever as this is intended to be used for language
			// research
			// and
			// we don't want to filter things out that could be found otherwise
			final Tokenizer src = new WhitespaceTokenizer(reader);
			TokenStream tok = new TrimFilter(src);
			tok = new StartEndTaggingFilter(tok,
					new IndexFormatProvider() {

						@Override
						public String produce(String tagName, String term) {
							return "#" + tagName + "_" + term;
						}

					},
					Pattern.compile("</#([a-zA-Z]+)>"),
					Pattern.compile("<#([a-zA-Z]+)>"), true);
			// we shouldn't lowercase here or use stopwordfilters, as this is
			// for
			// the analysis of texts with all its parts
			return new TokenStreamComponents(src, tok);
		}

	}

	public static class SimpleNextTokenTaggingAnalyzer extends Analyzer {

		@Override
		protected TokenStreamComponents createComponents(String fieldName,
				Reader reader) {
			// we use a tokenizer that doesn't remove dots,
			// hyphens or whatever as this is intended to be used for language
			// research
			// and
			// we don't want to filter things out that could be found otherwise
			final Tokenizer src = new WhitespaceTokenizer(reader);
			TokenStream tok = new TrimFilter(src);
			tok = new NextTokenTaggingFilter(tok, new IndexFormatProvider() {

				@Override
				public String produce(String tagName, String term) {
					return "#" + tagName + "_" + term;
				}

			}, Pattern.compile("<#([a-zA-Z]+)>"), true);
			// we shouldn't lowercase here or use stopwordfilters, as this is
			// for
			// the analysis of texts with all its parts
			return new TokenStreamComponents(src, tok);
		}
	}

	public void testStartEndTaggingFilter() {
		SimpleStartEndTaggingAnalyzer analyzer = new SimpleStartEndTaggingAnalyzer();
		String input = "<#word> This </#word> <#word> <#verb> is </#verb> a </#word> sentence";
		System.out.println(Tokenize.tokenizeString(analyzer, input));
		analyzer.close();
	}

	public void testNextTokenTaggingFilter() {
		SimpleNextTokenTaggingAnalyzer analyzer = new SimpleNextTokenTaggingAnalyzer();
		String input = "<#word> This <#verb> is a sentence";
		System.out.println(Tokenize.tokenizeString(analyzer, input));
		analyzer.close();
	}

}
