/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.filter.tagging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

/**
 * Base Class for TaggingFilters (tagging in the textfield itself, no custom
 * attributes). Implementing Classes should not tag empty strings
 * 
 * @author Martin Braun
 */
public abstract class TaggingFilter extends TokenFilter {

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
	private final TagAttribute tagAtt;

	protected final List<String> currentTags;

	private final IndexFormatProvider indexFormatProvider;
	private final boolean produceTaggedVersions;

	private boolean nonMarkerTokenFoundAndTagsAvailable;
	private int curTagIndex;

	private String curTerm;
	private int curPosLen;
	private int tokStart;
	private int tokEnd;

	/**
	 * @param input
	 *            The input TokenStream for this filter
	 * @param patternForStartTag
	 *            pattern for the start tag. has to contain one capturing group
	 *            to determine the tag name.
	 * @param patternForEndTag
	 *            pattern for the end tag. has to contain one capturing group to
	 *            determine the tag name.
	 * @param indexFormatProvider
	 *            the {@link IndexFormatProvider} to use with this filter
	 *            instance
	 * @param allowMarkerTokens
	 *            true if marker tokens (start and end tag) should be written
	 *            into the index. these will not be passed to the provided
	 *            {@link #indexFormatProvider}
	 */
	public TaggingFilter(TokenStream input,
			IndexFormatProvider indexFormatProvider,
			boolean produceTagAttribute, boolean produceTaggedVersions) {
		super(input);
		this.indexFormatProvider = indexFormatProvider;
		this.currentTags = new ArrayList<>();
		this.nonMarkerTokenFoundAndTagsAvailable = false;
		if (!produceTagAttribute && !produceTaggedVersions) {
			throw new IllegalArgumentException(
					"this filter should at least produce something "
							+ "(produceTagAttribute & produceTaggedVersions were equal to false");
		}
		if (produceTagAttribute) {
			this.tagAtt = this.addAttribute(TagAttribute.class);
		} else {
			this.tagAtt = null;
		}
		this.produceTaggedVersions = produceTaggedVersions;
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (this.curTerm == null) {
			if (!input.incrementToken()) {
				if (this.currentTags.size() > 0) {
					LOGGER.warning("end of input reached and POS tag "
							+ currentTags.toString()
							+ " was never closed/finished!");
				}
				return false;
			} else {
				this.curTerm = String.valueOf(this.termAtt);
				this.curPosLen = this.posLenAtt.getPositionLength();
				this.tokStart = this.offsetAtt.startOffset();
				this.tokEnd = this.offsetAtt.endOffset();
				this.curTagIndex = 0;
				this.nonMarkerTokenFoundAndTagsAvailable = false;
			}
		}

		if (this.nonMarkerTokenFoundAndTagsAvailable) {
			this.clearAttributes();
			if (this.curTagIndex < 0
					|| this.curTagIndex >= this.currentTags.size()) {
				throw new AssertionError(
						"curTagIndex < 0 or >= than currentTags.size()");
			}
			if (this.curTerm.equals("")) {
				// don't tag empty tokens
				// TODO: maybe move this call as we can stop handling stuff
				// earlier
				this.finishedHandlingTagsForCurrentToken();
				this.nextToken();
				return true;
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
			this.setTagAtts();
			// check if we have to produce another tagged version of the current
			// token. If not, go to the next token
			if (this.curTagIndex >= this.currentTags.size()) {
				this.finishedHandlingTagsForCurrentToken();
				this.nextToken();
			}
			return true;
		} else {
			boolean newTokenRet = this.handleNewToken(this.curTerm);
			if (this.nonMarkerTokenFoundAndTagsAvailable) {
				//set the tag atts for the original token as well :)
				this.setTagAtts();
			}
			if (!this.produceTaggedVersions
					&& this.nonMarkerTokenFoundAndTagsAvailable) {
				// we would produce tagged versions next
				// but we are not allowed to, so we immediately go to the next
				// token and behave as if we handled all our stuff
				this.finishedHandlingTagsForCurrentToken();
				// tagged versions should get skipped
				this.nextToken();
			} else if (this.produceTaggedVersions) {

			}
			return newTokenRet;
		}
	}

	/**
	 * called everytime a new token is read. here you can specify the logic that
	 * handles the original version of the token and controls whether we should
	 * produce taggedVersions or we should skip to the next token. this is
	 * called from the incrementToken method and should return values
	 * corresponding to that
	 * 
	 * @param curTerm
	 * @return
	 */
	protected abstract boolean handleNewToken(String curTerm);

	/**
	 * callback
	 */
	protected void finishedHandlingTagsForCurrentToken() {

	}

	/**
	 * delete the current token
	 */
	protected void deleteToken() {
		this.termAtt.setEmpty();
		// don't change the offset, posInc or posLen attribute, this has to be
		// the same as in the source
	}

	/**
	 * call this if you want to continue with the next token in the stream.
	 */
	protected final void nextToken() {
		this.curTagIndex = -1;
		this.nonMarkerTokenFoundAndTagsAvailable = false;
		this.curTerm = null;
	}

	/**
	 * call this if the tagged versions should be produced in the next call of
	 * incrementTokens()
	 */
	protected final void nonMarkerTokenFound() {
		if (this.currentTags.size() > 0) {
			this.curTagIndex = 0;
			this.nonMarkerTokenFoundAndTagsAvailable = true;
		} else {
			this.nextToken();
		}
	}

	private void setTagAtts() {
		if (this.tagAtt != null) {
			this.tagAtt.addTags(this.currentTags);
		}
	}

}
