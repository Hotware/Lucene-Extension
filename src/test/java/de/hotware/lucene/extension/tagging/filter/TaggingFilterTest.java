/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.tagging.filter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import de.hotware.lucene.extension.filter.tagging.NextTokenTaggingFilter;
import de.hotware.lucene.extension.filter.tagging.StartEndTaggingFilter;
import de.hotware.lucene.extension.filter.tagging.TagAttribute;
import junit.framework.TestCase;

public class TaggingFilterTest extends TestCase {

	public static class SimpleStartEndTaggingAnalyzer extends Analyzer {

		@Override
		protected TokenStreamComponents createComponents(String fieldName,
				Reader reader) {
			// we use a tokenizer that doesn't remove dots,
			// hyphens or whatever as this is intended to be used for language
			// research and we don't want to filter things out that could be
			// found otherwise
			final Tokenizer src = new WhitespaceTokenizer(reader);
			TokenStream tok = new TrimFilter(src);
			tok = new StartEndTaggingFilter(tok, (tagName, term) -> {
				return "#" + tagName + "_" + term;
			}, Pattern.compile("</#([a-zA-Z]+)>"),
					Pattern.compile("<#([a-zA-Z]+)>"), true, true, true);
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
			// research and we don't want to filter things out that could be
			// found otherwise
			final Tokenizer src = new WhitespaceTokenizer(reader);
			TokenStream tok = new TrimFilter(src);
			tok = new NextTokenTaggingFilter(tok, (tagName, term) -> {
				return "#" + tagName + "_" + term;
			}, Pattern.compile("<#([a-zA-Z]+)>"), true, true, false);
			// we shouldn't lowercase here or use stopwordfilters, as this is
			// for
			// the analysis of texts with all its parts
			return new TokenStreamComponents(src, tok);
		}
	}

	public void testStartEndTaggingFilter() {
		SimpleStartEndTaggingAnalyzer analyzer = new SimpleStartEndTaggingAnalyzer();
		String input = "<#word> This </#word> <#word> <#verb> is </#verb> a </#word> sentence";
		List<String> result = new ArrayList<String>();
		boolean foundTagAttTag = false;
		boolean foundTaggedVersion = false;
		try (TokenStream stream = analyzer.tokenStream(null, new StringReader(
				input))) {
			stream.reset();
			while (stream.incrementToken()) {
				String str = stream.getAttribute(CharTermAttribute.class)
						.toString();
				result.add(str);
				PositionIncrementAttribute posInc = stream
						.getAttribute(PositionIncrementAttribute.class);
				if (posInc.getPositionIncrement() == 0) {
					foundTaggedVersion = true;
				}
				TagAttribute tagAtt = stream.getAttribute(TagAttribute.class);
				if (tagAtt.getTags().size() > 0) {
					foundTagAttTag = true;
				}
				System.out.println("Info for token \"" + str
						+ "\", found tagAttTags: " + tagAtt.getTags());
			}
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new RuntimeException(e);
		}
		if (!foundTagAttTag) {
			fail("should have found a TagAttTag of at least one token");
		}
		if (!foundTaggedVersion) {
			fail("should have found tagged version of at least one token");
		}
		System.out.println(result);
		analyzer.close();
	}

	public void testNextTokenTaggingFilter() {
		SimpleNextTokenTaggingAnalyzer analyzer = new SimpleNextTokenTaggingAnalyzer();
		String input = "<#word> This test <#verb> is a sentence";
		List<String> result = new ArrayList<String>();
		boolean foundTagAttTag = false;
		try (TokenStream stream = analyzer.tokenStream(null, new StringReader(
				input))) {
			stream.reset();
			while (stream.incrementToken()) {
				String str = stream.getAttribute(CharTermAttribute.class)
						.toString();
				result.add(str);
				PositionIncrementAttribute posInc = stream
						.getAttribute(PositionIncrementAttribute.class);
				if (posInc.getPositionIncrement() == 0) {
					// we configured it to not return any tagged versions but it
					// did
					fail("no tagged versions should be returned at this point");
				}
				TagAttribute tagAtt = stream.getAttribute(TagAttribute.class);
				if (tagAtt.getTags().size() > 0) {
					foundTagAttTag = true;
				}
				System.out.println("Info for token \"" + str
						+ "\", found tagAttTags: " + tagAtt.getTags());
			}
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new RuntimeException(e);
		}
		if (!foundTagAttTag) {
			fail("should have found a TagAttTag of at least one token");
		}
		System.out.println(result);
		analyzer.close();
	}

}
