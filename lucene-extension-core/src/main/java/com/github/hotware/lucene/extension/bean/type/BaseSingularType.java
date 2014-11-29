/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.type;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;

/**
 * @author Martin Braun
 */
public abstract class BaseSingularType implements SingularFieldType {

	public BaseSingularType(NumericType numericType) {
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
