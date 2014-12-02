package com.github.hotware.lucene.extension.hsearch.dto;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.hotware.lucene.extension.hsearch.dto.annotations.DtoField;
import com.github.hotware.lucene.extension.util.Util;

public interface DtoDescriptor {

	public DtoDescription getFieldNames(Class<?> clazz);

	public final class DtoDescription {

		public static final String DEFAULT_PROFILE = (String) Util
				.getDefaultValueForAnnotationMethod(DtoField.class,
						"profileName");
		public static final String DEFAULT_FIELD_NAME = (String) Util
				.getDefaultValueForAnnotationMethod(DtoField.class, "fieldName");

		private final Class<?> entityClass;
		private final Map<String, List<FieldDescription>> fieldNamesForProfile;

		public DtoDescription(Class<?> entityClass,
				Map<String, List<FieldDescription>> fieldNamesForProfile) {
			super();
			this.entityClass = entityClass;
			this.fieldNamesForProfile = fieldNamesForProfile;
		}

		public List<FieldDescription> getFieldDescriptionsForProfile(String profile) {
			return Collections.unmodifiableList(this.fieldNamesForProfile
					.getOrDefault(profile, Collections.emptyList()));
		}

		public Class<?> getEntityClass() {
			return this.entityClass;
		}

		public static class FieldDescription {

			private final String fieldName;
			private final java.lang.reflect.Field field;

			public FieldDescription(String fieldName, Field field) {
				super();
				this.fieldName = fieldName;
				this.field = field;
			}

			public String getFieldName() {
				return fieldName;
			}

			public java.lang.reflect.Field getField() {
				return field;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((field == null) ? 0 : field.hashCode());
				result = prime * result
						+ ((fieldName == null) ? 0 : fieldName.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				FieldDescription other = (FieldDescription) obj;
				if (field == null) {
					if (other.field != null)
						return false;
				} else if (!field.equals(other.field))
					return false;
				if (fieldName == null) {
					if (other.fieldName != null)
						return false;
				} else if (!fieldName.equals(other.fieldName))
					return false;
				return true;
			}

		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((entityClass == null) ? 0 : entityClass.hashCode());
			result = prime
					* result
					+ ((fieldNamesForProfile == null) ? 0
							: fieldNamesForProfile.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DtoDescription other = (DtoDescription) obj;
			if (entityClass == null) {
				if (other.entityClass != null)
					return false;
			} else if (!entityClass.equals(other.entityClass))
				return false;
			if (fieldNamesForProfile == null) {
				if (other.fieldNamesForProfile != null)
					return false;
			} else if (!fieldNamesForProfile.equals(other.fieldNamesForProfile))
				return false;
			return true;
		}

	}

}
