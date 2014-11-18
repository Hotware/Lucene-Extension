package de.hotware.lucene.extension.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.util.AttributeImpl;

public class TagAttributeImpl extends AttributeImpl implements TagAttribute {

	private final Set<String> tags = new HashSet<>();

	@Override
	public Set<String> getTags() {
		return this.tags;
	}

	@Override
	public void addTags(Iterable<String> tags) {
		for(String tag : tags) {
			this.tags.add(tag);
		}
	}

	@Override
	public void addTags(String... tags) {
		this.addTags(Arrays.asList(tags));
	}

	@Override
	public void clear() {
		this.tags.clear();
	}

	@Override
	public void copyTo(AttributeImpl target) {
		((TagAttributeImpl) target).addTags(this.tags);
	}

}
