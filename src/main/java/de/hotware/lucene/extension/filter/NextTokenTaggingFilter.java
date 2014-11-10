package de.hotware.lucene.extension.filter;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;

/**
 * only tag the next real token in the stream
 * 
 * @author Martin Braun
 */
public final class NextTokenTaggingFilter extends TaggingFilter {

	private static final Logger LOGGER = Logger
			.getLogger(NextTokenTaggingFilter.class.getName());

	private final Pattern patternForTag;
	private final boolean allowMarkerTokens;

	public NextTokenTaggingFilter(TokenStream input,
			IndexFormatProvider indexFormatProvider, Pattern patternForTag,
			boolean allowMarkerTokens) {
		super(input, indexFormatProvider);
		if (patternForTag.matcher("").groupCount() != 1) {
			throw new IllegalArgumentException("pattern has to have exactly"
					+ " one capturing group in it");
		}
		this.patternForTag = patternForTag;
		this.allowMarkerTokens = allowMarkerTokens;
	}

	@Override
	protected boolean handleNewToken(String curTerm) {
		// handle a new token
		Matcher matcher = this.patternForTag.matcher(curTerm);

		boolean matchedOnce = false;

		if (matcher.matches()) {
			if (!matchedOnce) {
				matchedOnce = true;
			} else {
				throw new IllegalStateException(
						"already matched a start/end tag");
			}
			String tagName = matcher.group(1);
			if (this.currentTags.contains(tagName)) {
				LOGGER.warning("duplicate start of tag "
						+ this.patternForTag.toString());
			} else {
				this.currentTags.add(tagName);
			}
			LOGGER.info("tag found: " + curTerm);
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

	@Override
	protected void finishedProducingTokens() {
		// we already produced stuff and we only want the next token after all
		// the taggers to be tagged, so here we go
		this.currentTags.clear();
	}

}
