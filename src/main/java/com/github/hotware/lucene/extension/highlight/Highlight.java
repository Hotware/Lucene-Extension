package com.github.hotware.lucene.extension.highlight;

import java.util.List;

import org.apache.lucene.search.vectorhighlight.FieldFragList.WeightedFragInfo;

/**
 * @author Martin Braun
 */
public class Highlight {
	
	private final WeightedFragInfo fragInfo;
	private final List<String> texts;
	
	public Highlight(WeightedFragInfo fragInfo, List<String> texts) {
		this.fragInfo = fragInfo;
		this.texts = texts;
	}
	
	public WeightedFragInfo getFragInfo() {
		return this.fragInfo;
	}
	
	public List<String> getTexts() {
		return texts;
	}
	
}
