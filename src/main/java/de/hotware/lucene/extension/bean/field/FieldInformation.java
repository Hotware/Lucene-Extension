package de.hotware.lucene.extension.bean.field;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.lucene.document.FieldType;

import de.hotware.lucene.extension.bean.BeanField;

public final class FieldInformation {

	private final FrozenField field;
	private final Class<?> fieldClass;
	private final List<Type> genericTypeArgs;
	private final FieldType fieldType;
	private final BeanField beanField;

	protected FieldInformation(FrozenField field, Class<?> fieldClass,
			List<Type> genericTypeArgs, FieldType fieldType, BeanField beanField) {
		super();
		this.field = field;
		this.genericTypeArgs = genericTypeArgs;
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

	public List<Type> getGenericTypeArgs() {
		return this.genericTypeArgs;
	}

	@Override
	public String toString() {
		return "FieldInformation [field=" + field + ", fieldClass="
				+ fieldClass + ", genericTypeArgs=" + genericTypeArgs
				+ ", fieldType=" + fieldType + ", beanField=" + beanField + "]";
	}

}