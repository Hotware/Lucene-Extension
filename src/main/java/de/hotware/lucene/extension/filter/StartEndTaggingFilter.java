package de.hotware.lucene.extension.filter;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;

public final class StartEndTaggingFilter extends TaggingFilter {
	
	private static final Logger LOGGER = Logger.getLogger(StartEndTaggingFilter.class.getName());

	private final Pattern patternForStartTag;
	private final Pattern patternForEndTag;
	private final boolean allowMarkerTokens;

	public StartEndTaggingFilter(TokenStream input, IndexFormatProvider indexFormatProvider,
			Pattern patternForEndTag, Pattern patternForStartTag,
			boolean allowMarkerTokens) {
		super(input, indexFormatProvider);
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
			LOGGER.info("start tag found: " + curTerm);
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
			LOGGER.info("end tag found: " + curTerm);
		}

		if (!matchedOnce) {
			// first: return the original version, but make sure the next
			// time the tagged versions are returned
			if (this.currentTags.size() > 0) {
				this.produceTaggedVersions();
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
