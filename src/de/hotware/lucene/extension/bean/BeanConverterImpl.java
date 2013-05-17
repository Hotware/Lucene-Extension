package de.hotware.lucene.extension.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;

import de.hotware.lucene.extension.bean.BeanField.TypeWrapper;
import de.hotware.lucene.extension.bean.BeanInformationCache.FieldInformation;

/**
 * Basic Implementation of a BeanConverter (does caching of the
 * field-information)
 * 
 * @author Martin Braun
 */
public class BeanConverterImpl implements BeanConverter {

	private static final Set<Class<?>> PLAIN_TYPES;
	static {
		Set<Class<?>> tmp = new HashSet<Class<?>>();
		//all primitives and their wrappers
		tmp.add(Integer.class);
		tmp.add(Float.class);
		tmp.add(Double.class);
		tmp.add(Long.class);
		tmp.add(Character.class);
		tmp.add(Byte.class);
		tmp.add(Boolean.class);
		tmp.add(Short.class);
		tmp.add(int.class);
		tmp.add(float.class);
		tmp.add(double.class);
		tmp.add(long.class);
		tmp.add(char.class);
		tmp.add(byte.class);
		tmp.add(boolean.class);
		tmp.add(short.class);
		//and strings
		tmp.add(String.class);
		PLAIN_TYPES = Collections.unmodifiableSet(tmp);
	}

	private final BeanInformationCache cache;

	public BeanConverterImpl(BeanInformationCache cache) {
		this.cache = cache;
	}

	@Override
	public <T> T documentToBean(Class<T> clazz, Document document) {
		T ret;
		try {
			ret = clazz.newInstance();
		} catch(InstantiationException e) {
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		List<FieldInformation> fieldInformations = this.cache
				.getFieldInformations(clazz);
		boolean foundAnnotation = false;
		for(FieldInformation fieldInformation : fieldInformations) {
			foundAnnotation = true;
			Field field = fieldInformation.getField();
			BeanField bf = fieldInformation.getBeanField();
			Class<?> objectFieldType = fieldInformation.getFieldClass();
			TypeWrapper typeWrapper = bf.type();
			if(typeWrapper != TypeWrapper.SERIALIZED &&
					(!PLAIN_TYPES.contains(objectFieldType) &&
							objectFieldType.isArray() && !PLAIN_TYPES
								.contains(objectFieldType.getComponentType()))) {
				throw new IllegalArgumentException("only primitive types and "
						+ "their array-types are allowed");
			}
			String name = bf.name();
			if(name.equals(BeanField.DEFAULT_NAME)) {
				name = field.getName();
			}
			IndexableField[] indexFields = document.getFields(name);
			List<Object> values = new ArrayList<Object>();
			for(IndexableField cur : indexFields) {
				//TODO: maybe change this to the specific value-methods (to prevent all the parsing)
				values.add(typeWrapper.toBeanValue(cur));
			}
			//TODO: kinda dirty
			if(values.size() > 0) {
				if(objectFieldType.isArray()) {
					Class<?> arrayType = objectFieldType.getComponentType();
					int valuesCount = values.size();
					Object arr = Array.newInstance(arrayType, valuesCount);
					for(int i = 0; i < valuesCount; ++i) {
						Array.set(arr, i, values.get(i));
					}
					try {
						field.set(ret, arr);
					} catch(IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch(IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						field.set(ret, values.get(0));
					} catch(IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch(IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		if(!foundAnnotation) {
			throw new IllegalArgumentException("the given object is no correct bean");
		}
		return ret;
	}

	@Override
	public Document beanToDocument(Object bean) {
		Class<?> clazz = bean.getClass();
		List<FieldInformation> fieldInformations = this.cache
				.getFieldInformations(clazz);
		Document ret = new Document();
		boolean foundAnnotation = false;
		for(FieldInformation fieldInformation : fieldInformations) {
			foundAnnotation = true;
			Field field = fieldInformation.getField();
			BeanField bf = fieldInformation.getBeanField();
			Class<?> objectFieldType = fieldInformation.getFieldClass();
			FieldType fieldType = fieldInformation.getFieldType();
			TypeWrapper typeWrapper = bf.type();
			if(typeWrapper != TypeWrapper.SERIALIZED &&
					(!PLAIN_TYPES.contains(objectFieldType) &&
							objectFieldType.isArray() && !PLAIN_TYPES
								.contains(objectFieldType.getComponentType()))) {
				throw new IllegalArgumentException("only primitive types and "
						+ "their array-types are allowed");
			}
			String name = bf.name();
			if(name.equals(BeanField.DEFAULT_NAME)) {
				name = field.getName();
			}
			try {
				typeWrapper.addDocFields(ret,
						name,
						field.get(bean),
						fieldType,
						objectFieldType);
			} catch(IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		if(!foundAnnotation) {
			throw new IllegalArgumentException("the given object is no correct bean");
		}
		return ret;
	}

	@Override
	public Analyzer getAnalyzer(Class<?> clazz, String locale) {
		return this.cache.getPerFieldAnalyzerWrapper(clazz,
				this.cache.getFieldInformations(clazz),
				locale);
	}

}
