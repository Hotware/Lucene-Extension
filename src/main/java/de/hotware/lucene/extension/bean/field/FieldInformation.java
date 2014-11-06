/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.field;

import org.apache.lucene.document.FieldType;

import de.hotware.lucene.extension.bean.BeanField;

public final class FieldInformation {

	private final FrozenField field;
	private final Class<?> fieldClass;
	private final FieldType fieldType;
	private final BeanField beanField;

	protected FieldInformation(FrozenField field, Class<?> fieldClass,
			FieldType fieldType, BeanField beanField) {
		super();
		this.field = field;
		this.fieldClass = fieldClass;
		this.fieldType = fieldType;
		this.beanField = beanField;
	}

	public FrozenField getField() {
		return this.field;
	}

	/**
	 * you shouldn't need this, but if you need to, here it is.
	 */
	public Class<?> getFieldClass() {
		return this.fieldClass;
	}

	public FieldType getFieldType() {
		return this.fieldType;
	}

	public BeanField getBeanField() {
		return this.beanField;
	}

	@Override
	public String toString() {
		return "FieldInformation [field=" + field + ", fieldClass="
				+ fieldClass + ", fieldType=" + fieldType + ", beanField="
				+ beanField + "]";
	}

}