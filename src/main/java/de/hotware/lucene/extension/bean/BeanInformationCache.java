package de.hotware.lucene.extension.bean;

import java.lang.reflect.Field;
import java.util.List;
import java.lang.reflect.Type;

import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.FieldType;

/**
 * @author Martin Braun
 */
public interface BeanInformationCache {

	public List<FieldInformation> getFieldInformations(Class<?> clazz);

	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz);

	public static final class FieldInformation {

		private final Field field;
		private final Class<?> fieldClass;
		private final List<Type> genericTypeArgs;
		private final FieldType fieldType;
		private final BeanField beanField;

		protected FieldInformation(Field field,
				Class<?> fieldClass,
				List<Type> genericTypeArgs,
				FieldType fieldType,
				BeanField beanField) {
			super();
			this.field = field;
			this.genericTypeArgs = genericTypeArgs;
			this.fieldClass = fieldClass;
			this.fieldType = fieldType;
			this.beanField = beanField;
		}

		public Field getField() {
			return this.field;
		}

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
					+ ", fieldType=" + fieldType + ", beanField=" + beanField
					+ "]";
		}

	}

}
