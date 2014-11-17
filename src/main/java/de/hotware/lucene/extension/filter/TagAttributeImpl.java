package de.hotware.lucene.extension.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.util.AttributeImpl;

public class TagAttributeImpl extends AttributeImpl implements TagAttribute {
	
	private List<String> tags;

	@Override
	public List<String> getTags() {
		return this.tags;
	}

	@Override
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public void clear() {
		this.tags = null;
	}

	@Override
	public void copyTo(AttributeImpl target) {
		((TagAttributeImpl) target).tags = new ArrayList<>(this.tags);
	}

}
