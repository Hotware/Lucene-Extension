package de.hotware.lucene.extension.bean.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class SuggestAnalyzer extends Analyzer {
	
//  private static final String[] ENGLISH_STOP_WORDS = {
//  "a", "an", "and", "are", "as", "at", "be", "but", "by",
//  "for", "i", "if", "in", "into", "is",
//  "no", "not", "of", "on", "or", "s", "such",
//  "t", "that", "the", "their", "then", "there", "these",
//  "they", "this", "to", "was", "will", "with"
//  };

	
	private final Version version;
		
	public SuggestAnalyzer(Version version) {
		this.version = version;
	}

	@Override
	protected TokenStreamComponents createComponents(
			String fieldName, Reader reader) {
		Tokenizer tokenizer = new StandardTokenizer(version, reader);

		TokenStream result = new StandardFilter(version, tokenizer);
		result = new LowerCaseFilter(version, result);
//			result = new ISOLatin1AccentFilter(result);
//			result = new StopFilter(Version.LUCENE_45, result,
//				ENGLISH_STOP_WORDS);
		result = new EdgeNGramTokenFilter(version,
			result, 1, 20);

		return new TokenStreamComponents(tokenizer, result);
	}

}
