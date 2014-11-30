/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.standalone.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.document.FieldType;

import com.github.hotware.lucene.extension.bean.standalone.annotations.BeanField;
import com.github.hotware.lucene.extension.bean.standalone.annotations.BeanFields;
import com.github.hotware.lucene.extension.util.CacheMap;

/**
 * Reference Implementation for a BeanInformationCache
 * 
 * @author Martin Braun
 */
public class BeanInformationCacheImpl implements BeanInformationCache {

	public static final int DEFAULT_CACHE_SIZE = 1024;

	private final Map<Class<?>, List<FieldInformation>> annotatedFieldsCache;
	private final Lock annotatedFieldsCacheLock;

	/**
	 * calls {@link #BeanInformationCacheImpl(int)} with
	 * {@value #DEFAULT_CACHE_SIZE} as the default parameter
	 * 
	 * @see #BeanConverterImpl(int)
	 */
	public BeanInformationCacheImpl() {
		this(DEFAULT_CACHE_SIZE);
	}

	public BeanInformationCacheImpl(int cacheSize) {
		this.annotatedFieldsCacheLock = new ReentrantLock();
		this.annotatedFieldsCache = new CacheMap<Class<?>, List<FieldInformation>>(
				cacheSize);
	}

	@Override
	public List<FieldInformation> getFieldInformations(Class<?> clazz) {
		this.annotatedFieldsCacheLock.lock();
		try {
			List<FieldInformation> fieldInformations = this.annotatedFieldsCache
					.get(clazz);
			if (fieldInformations == null) {
				fieldInformations = new ArrayList<FieldInformation>();
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(BeanField.class)
							|| field.isAnnotationPresent(BeanFields.class)) {
						Class<?> fieldClass = field.getType();
						// TODO: apparently the genericTypes are never needed in
						// the BeanConverter API, so we don't need this anymore
						// users can always implement their own custom type
						// hierarchies
						
						// {
						// Type type = field.getGenericType();
						// if (type instanceof ParameterizedType) {
						// ParameterizedType parType = (ParameterizedType) type;
						// fieldClass = (Class<?>) parType.getRawType();
						// // TODO: maybe we want more?
						// // i.e. Map<String, List<Integer>>?
						// // we only cover one layer of generics as only
						// // the Primitives are allowed
						// // as types in the collections
						// } else if (type instanceof GenericArrayType) {
						// // cannot be handled differently
						// // will cause exceptions later in
						// // BeanConverterImpl
						// // but this is not the point of this class
						// // fieldClass = Object[].class;
						// fieldClass = (Class<?>) type;
						// } else {
						// fieldClass = (Class<?>) type;
						// }
						// // TODO: what about WildcardType and
						// // TypeVariable
						// }
						field.setAccessible(true);
						fieldInformations.addAll(this.getFieldInformations(
								field, fieldClass));
					}
				}
				this.annotatedFieldsCache.put(clazz, fieldInformations);
			}
			return fieldInformations;
		} finally {
			this.annotatedFieldsCacheLock.unlock();
		}
	}

	@Override
	public String toString() {
		return "BeanInformationCacheImpl [annotatedFieldsCache="
				+ annotatedFieldsCache + ", annotatedFieldsCacheLock="
				+ annotatedFieldsCacheLock + "]";
	}

	private List<FieldInformation> getFieldInformations(Field field,
			Class<?> fieldClass) {
		List<FieldInformation> infos = new ArrayList<>();
		if (field.isAnnotationPresent(BeanField.class)) {
			infos.add(this.buildFieldInformation(
					field.getAnnotation(BeanField.class), field, fieldClass));
		}
		if (field.isAnnotationPresent(BeanFields.class)) {
			BeanFields bfs = field.getAnnotation(BeanFields.class);
			if (bfs.value() != null) {
				for (BeanField bf : bfs.value()) {
					infos.add(this.buildFieldInformation(bf, field, fieldClass));
				}
			} else {
				throw new IllegalArgumentException(
						"BeanFields's value() is not allowed to be null!");
			}
		}
		return infos;
	}

	private FieldInformation buildFieldInformation(BeanField bf, Field field,
			Class<?> fieldClass) {
		com.github.hotware.lucene.extension.bean.standalone.type.Type typeWrapper;
		try {
			// TODO: maybe cache these?
			typeWrapper = (com.github.hotware.lucene.extension.bean.standalone.type.Type) bf
					.type().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		FieldType fieldType = new FieldType();
		fieldType.setIndexed(bf.index());
		fieldType.setStored(bf.store());
		fieldType.setTokenized(bf.tokenized());
		fieldType.setStoreTermVectors(bf.storeTermVectors());
		fieldType.setStoreTermVectorPositions(bf.storeTermVectorPositions());
		fieldType.setStoreTermVectorOffsets(bf.storeTermVectorOffsets());
		fieldType.setStoreTermVectorPayloads(bf.storeTermVectorPayloads());
		fieldType.setOmitNorms(bf.omitNorms());
		fieldType.setIndexOptions(bf.indexOptions());
		typeWrapper.configureFieldType(fieldType);
		fieldType.freeze();
		return new FieldInformation(new FrozenField(field), fieldClass,
				fieldType, bf);
	}

}
