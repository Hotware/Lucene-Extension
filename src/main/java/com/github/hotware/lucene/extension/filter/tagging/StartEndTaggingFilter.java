package com.github.hotware.lucene.extension.filter.tagging;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;

/**
 * Filter to process tokens between start and end tags. <br>
 * <br>
 * <br>
 * All Tokens between a start and end tag can be recognized. One use case would
 * be Part of Speech tagging. <br>
 * <br>
 * Tagging could be done in other ways, for example by providing custom
 * Attributes during Filtering, but for this one would have to provide a custom
 * {@link org.apache.lucene.search.Query} implementation. This could be worth
 * exploring if the performance of an index created with the help of this class
 * is insufficient.
 * 
 * @author Martin Braun
 */
public final class StartEndTaggingFilter extends TaggingFilter {

	private static final Logger LOGGER = Logger
			.getLogger(StartEndTaggingFilter.class.getName());

	private final Pattern patternForStartTag;
	private final Pattern patternForEndTag;
	private final boolean allowMarkerTokens;

	public StartEndTaggingFilter(TokenStream input,
			IndexFormatProvider indexFormatProvider, Pattern patternForEndTag,
			Pattern patternForStartTag, boolean allowMarkerTokens,
			boolean produceTagAttribute, boolean produceTaggedVersions) {
		super(input, indexFormatProvider, produceTagAttribute,
				produceTaggedVersions);
		if (patternForStartTag.matcher("").groupCount() != 1
				|| patternForEndTag.matcher("").groupCount() != 1) {
			throw new IllegalArgumentException(
					"start and end pattern have to have exactly"
							+ " one capturing group in them");
		}
		this.patternForStartTag = patternForStartTag;
		this.patternForEndTag = patternForEndTag;
		this.allowMarkerTokens = allowMarkerTokens;
	}

	@Override
	protected boolean handleNewToken(String curTerm) {
		// handle a new token
		Matcher startMatcher = this.patternForStartTag.matcher(curTerm);
		Matcher endMatcher = this.patternForEndTag.matcher(curTerm);

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
			LOGGER.finest("start tag found: " + curTerm);
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
			LOGGER.finest("end tag found: " + curTerm);
		}

		if (!matchedOnce) {
			// first: return the original version in this call, but make sure
			// the next time the tagged versions are returned
			this.nonMarkerTokenFound();
			return true;
		} else {
			if (!this.allowMarkerTokens) {
				// we apparently dont want the markers to be found in the
				// tokens
				this.deleteToken();
			}
			this.nextToken();
			return true;
		}
	}

}
