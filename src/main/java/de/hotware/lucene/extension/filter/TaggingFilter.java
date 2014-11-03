package de.hotware.lucene.extension.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

/**
 * Filter to process tokens between start and end tags. <br />
 * <br />
 * <br />
 * All Tokens between a start and end tag can be recognized. One use case would
 * be Part of Speech tagging, i.e.: <br />
 * <br />
 * This <#(verb)> is <#(end_of_verb)> a sentence. <br />
 * new TaggingFilter(input, Pattern.compile("<#(verb)>"),
 * Pattern.compile("<#(end_of_verb)>")) <br />
 * This will only index the "is" token in the sentence. All other tokens are set
 * to empty <br />
 * <br />
 * The pattern has to have one group.
 * 
 * @author Martin Braun
 */
public final class TaggingFilter extends TokenFilter {

	private static final Logger LOGGER = Logger.getLogger(TaggingFilter.class
			.getClass().getName());

	private final CharTermAttribute termAtt = this
			.addAttribute(CharTermAttribute.class);
	private final PositionIncrementAttribute posIncAtt = this
			.addAttribute(PositionIncrementAttribute.class);
	private final PositionLengthAttribute posLenAtt = this
			.addAttribute(PositionLengthAttribute.class);
	private final OffsetAttribute offsetAtt = this
			.addAttribute(OffsetAttribute.class);

	private final Pattern patternForStartTag;
	private final Pattern patternForEndTag;
	private final IndexFormatProvider indexFormatProvider;
	private final List<String> currentTags;
	private final boolean allowMarkerTokens;

	private boolean produceTaggedVersions;
	private int curTagIndex;

	private String curTerm;
	private char[] curBuffer;
	private int curPosLen;
	private int tokStart;
	private int tokEnd;

	public static interface IndexFormatProvider {

		public String produce(String tagName, String term);

	}

	public TaggingFilter(TokenStream input, Pattern patternForStartTag,
			Pattern patternForEndTag, IndexFormatProvider indexFormatProvider,
			boolean allowMarkerTokens) {
		super(input);
		if (patternForStartTag.matcher("").groupCount() != 1
				|| patternForEndTag.matcher("").groupCount() != 1) {
			throw new IllegalArgumentException(
					"start and end pattern have to have exactly"
							+ " one capturing group in them");
		}
		this.patternForStartTag = patternForStartTag;
		this.patternForEndTag = patternForEndTag;
		this.indexFormatProvider = indexFormatProvider;
		this.currentTags = new ArrayList<>();
		this.produceTaggedVersions = false;
		this.allowMarkerTokens = allowMarkerTokens;
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (this.curBuffer == null) {
			if (!input.incrementToken()) {
				if (this.currentTags.size() > 0) {
					LOGGER.warning("end of input reached and POS tag "
							+ currentTags.toString() + " were never closed!");
				}
				return false;
			} else {
				this.curTerm = String.valueOf(this.termAtt);
				this.curBuffer = this.termAtt.buffer().clone();
				this.curPosLen = this.posLenAtt.getPositionLength();
				this.tokStart = this.offsetAtt.startOffset();
				this.tokEnd = this.offsetAtt.endOffset();
				this.curTagIndex = 0;
				this.produceTaggedVersions = false;
			}
		}

		if (this.produceTaggedVersions) {
			this.clearAttributes();
			if (this.curTagIndex < 0
					|| this.curTagIndex >= this.currentTags.size()) {
				throw new AssertionError(
						"curTagIndex < 0 or >= than currentTags.size()");
			}
			String curTag = this.currentTags.get(this.curTagIndex++);
			// create the current tagged version.
			String taggedVersion = this.indexFormatProvider.produce(curTag,
					this.curTerm);
			this.termAtt.setEmpty();
			this.termAtt.append(taggedVersion);
			// all tags are supposed to be synonyms so...
			this.posIncAtt.setPositionIncrement(0);
			// this can just be copied
			this.posLenAtt.setPositionLength(this.curPosLen);
			this.offsetAtt.setOffset(this.tokStart, this.tokEnd);
			// check if we have to produce another tagged version of the current
			// token. If not, go to the next token
			if (this.curTagIndex >= this.currentTags.size()) {
				this.nextToken();
			}
			return true;
		} else {
			// handle a new token
			Matcher startMatcher = this.patternForStartTag
					.matcher(this.curTerm);
			Matcher endMatcher = this.patternForEndTag.matcher(this.curTerm);

			boolean matchedOnce = false;

			if (startMatcher.matches()) {
				if (!matchedOnce) {
					matchedOnce = true;
				} else {
					throw new IllegalStateException(
							"already matched a start/end tag");
				}
				String tagName = startMatcher.group(1);
				if (this.currentTags.contains(tagName)) {
					LOGGER.warning("duplicate start of tag "
							+ this.patternForStartTag.toString());
				} else {
					this.currentTags.add(tagName);
				}
				LOGGER.info("POS start tag found: " + this.curTerm);
			}

			if (endMatcher.matches()) {
				if (!matchedOnce) {
					matchedOnce = true;
				} else {
					throw new IllegalStateException(
							"already matched a start/end tag");
				}
				String tagName = endMatcher.group(1);
				if (!this.currentTags.contains(tagName)) {
					LOGGER.warning("end of tag found but no opening "
							+ "tag found before " + this.patternForEndTag);
				} else {
					this.currentTags.remove(tagName);
				}
				LOGGER.info("POS end tag found: " + this.curTerm);
			}

			if (!matchedOnce) {
				// first: return the original version, but make sure the next
				// time the tagged versions are returned
				if (this.currentTags.size() > 0) {
					this.curTagIndex = 0;
					this.produceTaggedVersions = true;
				} else {
					this.nextToken();
				}
				return true;
			} else {
				if (!this.allowMarkerTokens) {
					// we apparently dont want the markers to be found in the
					// tokens
					this.termAtt.setEmpty();
				}
				this.nextToken();
				return true;
			}
		}
	}

	private void nextToken() {
		this.curTagIndex = -1;
		this.produceTaggedVersions = false;
		this.curBuffer = null;
		this.curTerm = null;
	}

}
