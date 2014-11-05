/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.field;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.document.FieldType;

import de.hotware.lucene.extension.bean.BeanField;
import de.hotware.lucene.extension.bean.BeanFields;

/**
 * Reference Implementation for a BeanInformationCache
 * 
 * @author Martin Braun
 */
public class BeanInformationCacheImpl implements BeanInformationCache {

	public static final int DEFAULT_CACHE_SIZE = 1000;

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
						Class<?> fieldClass;
						List<Type> genericTypes = new ArrayList<Type>();
						{
							Type type = field.getGenericType();
							if (type instanceof ParameterizedType) {
								ParameterizedType parType = (ParameterizedType) type;
								fieldClass = (Class<?>) parType.getRawType();
								// TODO: maybe we want more? i.e. Map<String,
								// List<Integer>>?
								// we only cover one layer of generics as only
								// the Primitives are allowed
								// as types in the collections
								Type[] genericTypeArgs = ((ParameterizedType) type)
										.getActualTypeArguments();
								genericTypes.addAll(Arrays
										.asList(genericTypeArgs));
							} else if (type instanceof GenericArrayType) {
								// cannot be handled differently
								// will cause exceptions later in
								// BeanConverterImpl
								// but this is not the point of this class
								fieldClass = Object[].class;
							} else {
								fieldClass = (Class<?>) type;
							}
							// TODO: what about WildcardType and TypeVariable
						}
						field.setAccessible(true);
						fieldInformations.addAll(this.getFieldInformations(
								field, fieldClass, genericTypes));
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
			Class<?> fieldClass, List<Type> genericTypes) {
		List<FieldInformation> infos = new ArrayList<>();
		if (field.isAnnotationPresent(BeanField.class)) {
			infos.add(this.buildFieldInformation(
					field.getAnnotation(BeanField.class), field, fieldClass,
					genericTypes));
		}
		if (field.isAnnotationPresent(BeanFields.class)) {
			BeanFields bfs = field.getAnnotation(BeanFields.class);
			if (bfs.value() != null) {
				for (BeanField bf : bfs.value()) {
					infos.add(this.buildFieldInformation(bf, field, fieldClass,
							genericTypes));
				}
			} else {
				throw new IllegalArgumentException(
						"BeanFields's value() is not allowed to be null!");
			}
		}
		return infos;
	}

	private FieldInformation buildFieldInformation(BeanField bf, Field field,
			Class<?> fieldClass, List<Type> genericTypes) {
		de.hotware.lucene.extension.bean.type.Type typeWrapper;
		try {
			// TODO: maybe cache these?
			typeWrapper = (de.hotware.lucene.extension.bean.type.Type) bf
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
				Collections.unmodifiableList(genericTypes), fieldType, bf);
	}

	private static final class CacheMap<K, V> extends LinkedHashMap<K, V> {

		private final int size;

		public CacheMap(int size) {
			this.size = size;
		}

		private static final long serialVersionUID = 2690314341945452137L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return this.size() > this.size;
		}

		@Override
		public String toString() {
			return "CacheMap [size=" + size + "]";
		}

	}

}
