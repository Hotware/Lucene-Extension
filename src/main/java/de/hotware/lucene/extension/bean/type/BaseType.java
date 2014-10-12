package de.hotware.lucene.extension.bean.type;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;

/**
 * @author Martin Braun
 */
public abstract class BaseType implements Type {

	public BaseType(NumericType numericType) {
		this.numericType = numericType;
	}

	protected final NumericType numericType;

	@Override
	public void configureFieldType(FieldType fieldType) {
		fieldType.setNumericType(this.getNumericType());
	}

	public NumericType getNumericType() {
		return this.numericType;
	}
	
}
