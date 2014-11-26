package com.github.hotware.lucene.extension.hsearch.internal;

import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Analyzer;

@Indexed
@ClassBridge(impl = BeanConverterFieldBridge.class, analyzer = @Analyzer(impl = BeanConverterWrappingAnalyzer.class))
public class HibernateSearchDTO {

	public static final String ID_FIELD_NAME = "HibernateSearchDTO__id";

	// we don't need it, but yeah lets keep it to = 0;
	private int id;

	// this is the real object we want to index
	private Object objectToIndex;

	@DocumentId(name = ID_FIELD_NAME)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Object getObjectToIndex() {
		return objectToIndex;
	}

	public void setObjectToIndex(Object objectToIndex) {
		this.objectToIndex = objectToIndex;
	}

}
