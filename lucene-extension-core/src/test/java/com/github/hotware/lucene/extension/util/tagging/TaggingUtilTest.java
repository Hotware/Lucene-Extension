package com.github.hotware.lucene.extension.util.tagging;

import java.util.ArrayList;
import java.util.List;

import com.github.hotware.lucene.extension.util.tagging.NextPositionTag;
import com.github.hotware.lucene.extension.util.tagging.NextPositionTagImpl;
import com.github.hotware.lucene.extension.util.tagging.TaggingUtil;

import junit.framework.TestCase;

public class TaggingUtilTest extends TestCase {

	public void testNextPositionTagging() {
		@SuppressWarnings("serial")
		List<NextPositionTag> tags = new ArrayList<NextPositionTag>() {
			{
				this.add(new NextPositionTagImpl(0, "S"));
				this.add(new NextPositionTagImpl(5, "N"));
				this.add(new NextPositionTagImpl(8, "K"));
			}
		};
		String text = "This is a Test";
		String taggedText = TaggingUtil.writeNextTokenTagsIntoText(text, tags, (tagName) -> {
			return tagName;
		});
		assertEquals("S This N is K a Test", taggedText);
	}
	
	public void testStartEndPositionTagging() {
		@SuppressWarnings("serial")
		List<StartEndTag> tags = new ArrayList<StartEndTag>() {
			{
				this.add(new StartEndTagImpl(0, 4, "S" ));
				this.add(new StartEndTagImpl(5, 7, "N" ));
				this.add(new StartEndTagImpl(10, 14, "K" ));
			}
		};
		String text = "This is a Test";
		String taggedText = TaggingUtil.writeStartEndTokenTagsIntoText(text, tags, (tagName) -> {
			return String.format("<%s>", tagName);
		}, (tagName) -> {
			return String.format("</%s>", tagName);
		});
		assertEquals("<S> This </S> <N> is </N> a <K> Test </K>", taggedText);
	}

}
