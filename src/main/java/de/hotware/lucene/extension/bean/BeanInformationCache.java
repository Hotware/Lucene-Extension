package de.hotware.lucene.extension.bean;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.FieldType;

/**
 * @author Martin Braun
 */
public interface BeanInformationCache {

	public List<FieldInformation> getFieldInformations(Class<?> clazz);

	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz,
			List<FieldInformation> fieldInformations, String locale);

	public static final class FieldInformation {

		private final Field field;
		private final Class<?> fieldClass;
		private final FieldType fieldType;
		private final BeanField beanField;

		protected FieldInformation(Field field,
				Class<?> fieldClass,
				FieldType fieldType,
				BeanField beanField) {
			super();
			this.field = field;
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

	}

	public static final class CacheMap<K, V> extends LinkedHashMap<K, V> {

		private final int size;

		public CacheMap(int size) {
			this.size = size;
		}

		private static final long serialVersionUID = 2690314341945452137L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return this.size() > this.size;
		}

	}

}
