package com.github.hotware.lucene.extension.bean.hsearch;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.hibernate.search.engine.metadata.impl.DocumentFieldMetadata;
import org.hibernate.search.engine.metadata.impl.TypeMetadata;

import com.github.hotware.lucene.extension.bean.converter.DocumentToBeanConverter;
import com.github.hotware.lucene.extension.bean.hsearch.annotations.DtoField;
import com.github.hotware.lucene.extension.bean.hsearch.annotations.DtoOverEntity;

public class HibernateSearchDocumentToBeanConverter implements
		DocumentToBeanConverter {

	private final Map<Class<?>, TypeMetadata> typeMetadata;

	public HibernateSearchDocumentToBeanConverter(
			Map<Class<?>, TypeMetadata> typeMetadata) {
		this.typeMetadata = typeMetadata;
	}

	@Override
	public <T> T documentToBean(Class<T> clazz, Document document) {
		T ret;
		try {
			ret = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		DtoOverEntity[] dtoOverEntity = clazz
				.getAnnotationsByType(DtoOverEntity.class);
		if (dtoOverEntity.length != 1) {
			throw new IllegalArgumentException(
					"clazz must specify exactly one "
							+ "DtoOverEntity annotation at a class level");
		}
		java.lang.reflect.Field[] declared = clazz.getDeclaredFields();
		// we actually just want the FieldBridgess
		Map<String, FieldBridge> fieldNameToFieldBridge = this.typeMetadata
				.get(clazz)
				.getAllDocumentFieldMetadata()
				.stream()
				.collect(
						Collectors.toMap(
								(DocumentFieldMetadata docFieldMeta) -> {
									return docFieldMeta.getName();
								}, (DocumentFieldMetadata docFieldMeta) -> {
									return docFieldMeta.getFieldBridge();
								}));
		//let's do this
		Arrays.asList(declared)
				.forEach(
						(field) -> {
							//should be accessible :)
							field.setAccessible(true);
							DtoField annotation = field
									.getAnnotation(DtoField.class);
							if (annotation != null) {
								String fieldName = annotation.fieldName();
								FieldBridge fieldBridge = fieldNameToFieldBridge
										.get(fieldName);
								if (!(fieldBridge instanceof TwoWayFieldBridge)) {
									TwoWayFieldBridge twoWay = (TwoWayFieldBridge) fieldBridge;
									Object value = twoWay.get(fieldName,
											document);
									try {
										field.set(ret, value);
									} catch (IllegalAccessException e) {
										throw new RuntimeException(e);
									}
								} else {
									throw new IllegalArgumentException(
											"if you want to retrieve a Field from the "
													+ "Document, you have to annotate the field with a TwoWayFieldBridge");
								}
							}
						});
		return ret;
	}
}
