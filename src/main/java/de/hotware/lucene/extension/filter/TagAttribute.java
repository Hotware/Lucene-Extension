package de.hotware.lucene.extension.filter;

import java.util.List;

import org.apache.lucene.util.Attribute;

public interface TagAttribute extends Attribute {
	
	public List<String> getTags();
	
	public void setTags(List<String> tag);

}
