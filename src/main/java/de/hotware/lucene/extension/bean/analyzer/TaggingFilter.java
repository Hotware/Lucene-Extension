package de.hotware.lucene.extension.bean.analyzer;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Filter to process tokens between start and end tags. <br />
 * <br />
 * <br />
 * All Tokens between a start and end tag can be recognized. One use case would
 * be Part of Speech tagging, i.e.: <br />
 * <br />
 * This <#verb> is <#end_of_verb> a sentence. <br />
 * new TaggingFilter(input, Pattern.compile("<#verb>"),
 * Pattern.compile("<#end_of_verb>")) <br />
 * This will only index the is token in the sentence. All other tokens are
 * ignored.
 * 
 * @author Martin Braun
 */
public final class TaggingFilter extends TokenFilter {

	private static final Logger LOGGER = Logger.getLogger(TaggingFilter.class
			.getClass().getName());

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final Pattern posStartPattern;
	private final Pattern posEndPattern;
	private boolean isInPos;

	public TaggingFilter(TokenStream input, Pattern posStartPattern,
			Pattern posEndPattern) {
		super(input);
		this.posStartPattern = posStartPattern;
		this.posEndPattern = posEndPattern;
		this.isInPos = false;
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (!input.incrementToken()) {
			if (this.isInPos) {
				LOGGER.warning("end of input reached and POS tag "
						+ posStartPattern.toString() + " was never closed!");
			}
			return false;
		}

		Matcher startMatcher = this.posStartPattern.matcher(this.termAtt);
		Matcher endMatcher = this.posEndPattern.matcher(this.termAtt);

		boolean matchedOnce = false;

		if (startMatcher.matches()) {
			if (!matchedOnce) {
				matchedOnce = true;
			} else {
				throw new IllegalStateException(
						"already matched a start/end tag");
			}
			if (this.isInPos) {
				LOGGER.warning("duplicate start of tag "
						+ posStartPattern.toString());
			}
			this.isInPos = true;
			LOGGER.info("POS start tag found: " + posStartPattern.toString());
		}

		if (endMatcher.matches()) {
			if (!matchedOnce) {
				matchedOnce = true;
			} else {
				throw new IllegalStateException(
						"already matched a start/end tag");
			}
			if (!this.isInPos) {
				LOGGER.warning("end of tag found but no opening tag found before "
						+ posEndPattern.toString());
			}
			this.isInPos = false;
			LOGGER.info("POS end tag found: " + posStartPattern.toString());
		}

		if (!matchedOnce) {
			// this matches no start/end tag so it's input between or outside
			// the tags decide whether it should be put into the index or not
			// here
			if (this.isInPos) {
				// this termAtt is in the part of speech (yay)
			} else {
				this.termAtt.setEmpty();
			}
		} else {
			//we dont want the markers to be found in the tokens
			this.termAtt.setEmpty();
		}

		return true;
	}

}
