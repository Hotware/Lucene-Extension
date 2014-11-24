package com.github.hotware.lucene.extension.highlight;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Field;
import org.apache.lucene.search.vectorhighlight.FieldFragList.WeightedFragInfo;

/**
 * "Encodes" into Highlights containing all the hit information. In order to get
 * through the hits you will have to write your own logic outside of this class
 * to convert the Highlight Objects into something more usable
 * 
 * @author Martin Braun
 */
public class HighlightEncoder implements ObjectEncoder<Highlight> {

	@Override
	public Highlight encode(WeightedFragInfo fragInfo, Field[] values) {
		List<String> texts = new ArrayList<>();
		for(Field field : values) {
			texts.add(field.stringValue());
		}
		return new Highlight(fragInfo, texts);
	}

}
