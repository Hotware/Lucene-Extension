package de.hotware.lucene.extension.bean;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.FieldType;

import de.hotware.lucene.extension.bean.analyzer.AnalyzerProvider;

/**
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
		this.annotatedFieldsCache = new CacheMap<Class<?>, List<FieldInformation>>(cacheSize);
	}

	@Override
	public List<FieldInformation> getFieldInformations(Class<?> clazz) {
		this.annotatedFieldsCacheLock.lock();
		try {
			List<FieldInformation> fieldInformations = this.annotatedFieldsCache
					.get(clazz);
			if(fieldInformations == null) {
				fieldInformations = new ArrayList<FieldInformation>();
				Field[] fields = clazz.getDeclaredFields();
				for(Field field : fields) {
					if(field.isAnnotationPresent(BeanField.class)) {
						Class<?> fieldClass;
						List<Type> genericTypes = new ArrayList<Type>();
						{
							Type type = field.getGenericType();
							if(type instanceof ParameterizedType) {
								ParameterizedType parType = (ParameterizedType) type;
								fieldClass = (Class<?>) parType.getRawType();
								//TODO: maybe we want more? i.e. Map<String, List<Integer>>?
								//we only cover one layer of generics as only the Primitives are allowed
								//as types in the collections
								Type[] genericTypeArgs = ((ParameterizedType) type)
										.getActualTypeArguments();
								genericTypes.addAll(Arrays
										.asList(genericTypeArgs));
							} else if(type instanceof GenericArrayType) {
								//cannot be handled differently
								//will cause exceptions later in BeanConverterImpl
								//but this is not the point of this class
								fieldClass = Object[].class;
							} else {
								fieldClass = (Class<?>) type;
							}
							//TODO: what about WildcardType and TypeVariable
						}
						field.setAccessible(true);
						BeanField bf = field.getAnnotation(BeanField.class);
						de.hotware.lucene.extension.bean.type.Type typeWrapper;
						try {
							//TODO: maybe cache these?
							typeWrapper = (de.hotware.lucene.extension.bean.type.Type) bf
									.type().newInstance();
						} catch(InstantiationException | IllegalAccessException e) {
							throw new RuntimeException(e);
						}
						FieldType fieldType = new FieldType();
						fieldType.setIndexed(bf.index());
						fieldType.setStored(bf.store());
						fieldType.setTokenized(bf.tokenized());
						typeWrapper.configureFieldType(fieldType);
						fieldType.freeze();
						FieldInformation fieldInformation = new FieldInformation(field,
								fieldClass,
								Collections.unmodifiableList(genericTypes),
								fieldType,
								bf);
						fieldInformations.add(fieldInformation);
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
	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz) {
		Analyzer defaultAnalyzer = Constants.DEFAULT_ANALYZER;
		Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();
		for(FieldInformation info : this.getFieldInformations(clazz)) {
			String fieldName = info.getField().getName();
			BeanField bf = info.getBeanField();
			Analyzer analyzer;
			try {
				analyzer = ((AnalyzerProvider) bf.analyzerProvider()
						.newInstance()).getAnalyzer();
			} catch(InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			if(!analyzer.equals(defaultAnalyzer)) {
				fieldAnalyzers.put(fieldName, analyzer);
			}
		}
		return new PerFieldAnalyzerWrapper(defaultAnalyzer, fieldAnalyzers);
	}

	@Override
	public String toString() {
		return "BeanInformationCacheImpl [annotatedFieldsCache=" +
				annotatedFieldsCache + ", annotatedFieldsCacheLock=" +
				annotatedFieldsCacheLock + "]";
	}

}
