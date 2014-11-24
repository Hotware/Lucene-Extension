package com.github.hotware.lucene.extension.util.tagging;

import java.util.List;
import java.util.function.Function;

public final class TaggingUtil {

	private TaggingUtil() {
		throw new AssertionError("can't touch this!");
	}

	public static final NextPositionTag SURROGATE_START = new NextPositionTag() {

		@Override
		public int getStart() {
			return 0;
		}

		@Override
		public String getName() {
			throw new AssertionError("just a surrogate!");
		}

	};

	/**
	 * generates a Text with tags that can get recognized by
	 * {@link com.github.hotware.lucene.extension.filter.tagging.NextTokenTaggingFilter}
	 * 
	 * @param originalText
	 *            the original text
	 * @param tags
	 *            list of tags. only one word can be tagged with one tag!,
	 * @param tagFormatProvider
	 *            determines how the tags will be stored in the index (tagName)
	 *            -> taggedVersion
	 * @return the tagged text
	 */
	public static String writeNextTokenTagsIntoText(String originalText,
			List<NextPositionTag> tags,
			Function<String, String> tagFormatProvider) {
		if (tags.size() > 0) {
			final char[] originalTextBuf = originalText.toCharArray();
			// at least as long as the input text
			StringBuilder stringBuilder = new StringBuilder(
					originalTextBuf.length);
			NextPositionTag lastTag = SURROGATE_START;
			for (NextPositionTag tag : tags) {
				// write the text after the last tag into the builder
				stringBuilder.append(originalTextBuf, lastTag.getStart(),
						tag.getStart() - lastTag.getStart());
				// write the current tag into the builder
				String formattedTag = tagFormatProvider.apply(tag.getName());
				stringBuilder.append(formattedTag);
				stringBuilder.append(" ");
				lastTag = tag;
			}
			;
			if (lastTag != SURROGATE_START) {
				//and add the remaining text from the last last tag :P
				stringBuilder.append(originalTextBuf, lastTag.getStart(),
						originalTextBuf.length - lastTag.getStart());
			}
			return stringBuilder.toString();
		} else {
			return originalText;
		}
	}
	
}
