package de.hotware.lucene.extension.bean.type;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;


public interface Type {
	
	public void  handleDocFieldValue(Document document,
			String name,
			Object value,
			FieldType fieldType,
			Class<?> objectFieldType);
	
	public Object toBeanValue(IndexableField field);
	
	public void configureFieldType(FieldType fieldType);

}
