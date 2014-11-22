package de.hotware.lucene.extension.util.tagging;

import java.util.ArrayList;
import java.util.List;

import de.hotware.lucene.extension.util.tagging.NextPositionTag;
import de.hotware.lucene.extension.util.tagging.TaggingUtil;
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

}
