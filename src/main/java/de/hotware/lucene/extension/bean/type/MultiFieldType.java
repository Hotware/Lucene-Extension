package de.hotware.lucene.extension.bean.type;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;

/**
 * a MultiFieldType can have its information spread into more than one field in the
 * document. this is done by writing a StoredField with
 * <name_of_the_field>=<name_of_the_field>. This is needed for the BeanConverter
 * to recognize the Field.
 * 
 * @author Martin Braun
 */
public abstract class MultiFieldType implements Type {

	public final void handleDocFieldValues(Document document, String fieldName,
			Iterable<Object> values, FieldType fieldType, Class<?> objectFieldType) {
		document.add(new StoredField(fieldName, fieldName));
		this.handleMultiDocFieldValue(document, fieldName, values, fieldType,
				objectFieldType);
	}

	protected abstract void handleMultiDocFieldValue(Document document,
			String name, Iterable<Object> values, FieldType fieldType,
			Class<?> objectFieldType);
	
	/**
	 * @return may not be null
	 */
	public abstract Collection<Object> toBeanValues(Document document, String fieldName);

}
