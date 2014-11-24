/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;

import com.github.hotware.lucene.extension.bean.analyzer.AnalyzerProvider;
import com.github.hotware.lucene.extension.bean.annotations.BeanField;
import com.github.hotware.lucene.extension.bean.field.BeanInformationCache;
import com.github.hotware.lucene.extension.bean.field.FieldInformation;
import com.github.hotware.lucene.extension.bean.field.FrozenField;
import com.github.hotware.lucene.extension.bean.type.AnyClassType;
import com.github.hotware.lucene.extension.bean.type.MultiFieldType;
import com.github.hotware.lucene.extension.bean.type.SingularFieldType;
import com.github.hotware.lucene.extension.bean.type.Type;
import com.github.hotware.lucene.extension.util.CacheMap;

/**
 * Basic Implementation of a BeanConverter (does caching of the
 * field-information)
 * 
 * @author Martin Braun
 */
public class BeanConverterImpl implements BeanConverter {

	public static final int DEFAULT_PER_FIELD_ANALYZER_WRAPPER_CACHE_SIZE = 1024;
	private final static Logger LOGGER = Logger
			.getLogger(BeanConverterImpl.class.getName());

	private static final Set<Class<?>> PLAIN_TYPES;
	static {
		{
			Set<Class<?>> tmp = new HashSet<Class<?>>();
			// all primitives and their wrappers
			tmp.add(Integer.class);
			tmp.add(Float.class);
			tmp.add(Double.class);
			tmp.add(Long.class);
			tmp.add(Boolean.class);
			tmp.add(int.class);
			tmp.add(float.class);
			tmp.add(double.class);
			tmp.add(long.class);
			tmp.add(boolean.class);
			// and strings
			tmp.add(String.class);
			PLAIN_TYPES = Collections.unmodifiableSet(tmp);
		}
	}

	private static final Set<Class<?>> COLLECTION_TYPES;
	static {
		{
			Set<Class<?>> tmp = new HashSet<Class<?>>();
			// and one dimensional collections
			tmp.add(List.class);
			tmp.add(Set.class);
			// and maps
			// TODO: maybe add this feature later.
			// too time consuming and many changes in
			// the other classes have to be made
			// tmp.add(Map.class);
			COLLECTION_TYPES = Collections.unmodifiableSet(tmp);
		}
	}

	private static final Set<Class<?>> ALL_TYPES;
	static {
		{
			Set<Class<?>> tmp = new HashSet<Class<?>>();
			tmp.addAll(PLAIN_TYPES);
			tmp.addAll(COLLECTION_TYPES);
			ALL_TYPES = Collections.unmodifiableSet(tmp);
		}
	}

	private static final Map<Class<?>, TypeHandler> TYPE_HANDLER;
	static {
		{
			Map<Class<?>, TypeHandler> tmp = new HashMap<Class<?>, TypeHandler>();
			for (Class<?> val : ALL_TYPES) {
				tmp.put(val, TypeHandler.DEFAULT);
			}
			tmp.put(List.class, TypeHandler.LIST);
			tmp.put(Set.class, TypeHandler.SET);
			// TODO: maybe add this feature later.
			// too time consuming and many changes in
			// the other classes have to be made
			// tmp.put(Map.class, TypeHandler.MAP);
			TYPE_HANDLER = Collections.unmodifiableMap(tmp);
		}
	}

	private final ReentrantLock lock;
	private final BeanInformationCache cache;
	private final CacheMap<Class<?>, PerFieldAnalyzerWrapper> perFieldAnalyzerWrapperCache;

	public BeanConverterImpl(BeanInformationCache cache) {
		this(cache, DEFAULT_PER_FIELD_ANALYZER_WRAPPER_CACHE_SIZE);
	}

	public BeanConverterImpl(BeanInformationCache cache,
			int perFieldAnalyzerWrapperCacheSize) {
		this.lock = new ReentrantLock();
		this.cache = cache;
		this.perFieldAnalyzerWrapperCache = new CacheMap<>(
				perFieldAnalyzerWrapperCacheSize);
	}

	@Override
	public <T> T documentToBean(Class<T> clazz, Document document) {
		T ret;
		try {
			ret = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		List<FieldInformation> fieldInformations = this.cache
				.getFieldInformations(clazz);
		boolean foundAnnotation = false;
		for (FieldInformation fieldInformation : fieldInformations) {
			foundAnnotation = true;
			// only call this for fieldInformations with store = true
			if (fieldInformation.getBeanField().store()) {
				TypeHandler typeHandler = this.getTypeHandler(fieldInformation);
				typeHandler.writeDocumentInfoToBean(fieldInformation, document,
						ret);
			}
		}
		if (!foundAnnotation) {
			throw new IllegalArgumentException(
					"the given object is no correct bean");
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
		for (FieldInformation fieldInformation : fieldInformations) {
			foundAnnotation = true;
			TypeHandler typeHandler = this.getTypeHandler(fieldInformation);
			typeHandler.writeBeanInfoToDocument(fieldInformation, bean, ret);
		}
		if (!foundAnnotation) {
			throw new IllegalArgumentException(
					"the given object is no correct bean");
		}
		return ret;
	}

	@Override
	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz) {
		this.lock.lock();
		try {
			PerFieldAnalyzerWrapper ret;
			if (!this.perFieldAnalyzerWrapperCache.containsKey(clazz)) {
				Analyzer defaultAnalyzer = Constants.DEFAULT_ANALYZER;
				Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();
				for (FieldInformation info : this.cache
						.getFieldInformations(clazz)) {
					BeanField bf = info.getBeanField();
					String fieldName = bf.name();
					if (fieldName.equals(Constants.DEFAULT_NAME)) {
						fieldName = info.getField().getName();
					}
					Analyzer analyzer;
					try {
						analyzer = ((AnalyzerProvider) bf.analyzerProvider()
								.newInstance()).getAnalyzer(info);
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					if (!analyzer.equals(defaultAnalyzer)) {
						fieldAnalyzers.put(fieldName, analyzer);
					}
				}
				ret = new PerFieldAnalyzerWrapper(defaultAnalyzer,
						fieldAnalyzers);
				this.perFieldAnalyzerWrapperCache.put(clazz, ret);
			} else {
				ret = this.perFieldAnalyzerWrapperCache.get(clazz);
			}
			return ret;
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public String toString() {
		return "BeanConverterImpl [lock=" + lock + ", cache=" + cache
				+ ", perFieldAnalyzerWrapperCache="
				+ perFieldAnalyzerWrapperCache + "]";
	}

	private TypeHandler getTypeHandler(FieldInformation fieldInformation) {
		BeanField bf = fieldInformation.getBeanField();
		Class<?> objectFieldClass = fieldInformation.getFieldClass();
		Class<?> typeWrapper = bf.type();
		TypeHandler typeHandler;
		if (AnyClassType.class.isAssignableFrom(typeWrapper)) {
			// handle this as a default object
			typeHandler = TypeHandler.DEFAULT;
		} else if (!AnyClassType.class.isAssignableFrom(typeWrapper)
				&& !ALL_TYPES.contains(objectFieldClass)) {
			throw new IllegalArgumentException(
					"type of Java-Bean field not supported (maybe your custom type "
							+ "is no subclass of AnyClassType?): "
							+ fieldInformation.getField().getType());
		} else {
			// use one of our supported TypeHandlers.
			typeHandler = TYPE_HANDLER.get(objectFieldClass);
		}
		return typeHandler;
	}

	private static enum TypeHandler {
		DEFAULT {

			@Override
			public void writeBeanInfoToDocument(
					FieldInformation fieldInformation, Object origin,
					Document dest) {
				FrozenField field = fieldInformation.getField();
				BeanField bf = fieldInformation.getBeanField();
				Class<?> objectFieldType = fieldInformation.getFieldClass();
				FieldType fieldType = fieldInformation.getFieldType();
				Type typeWrapper;
				try {
					typeWrapper = (Type) bf.type().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				String name = bf.name();
				if (name.equals(Constants.DEFAULT_NAME)) {
					name = field.getName();
				}

				try {
					Object value = field.get(origin);
					if (value != null) {
						if (typeWrapper instanceof SingularFieldType) {
							((SingularFieldType) typeWrapper)
									.handleDocFieldValue(dest, name,
											field.get(origin), fieldType,
											objectFieldType);
						} else if (typeWrapper instanceof MultiFieldType) {
							((MultiFieldType) typeWrapper)
									.handleDocFieldValues(dest, name,
											Arrays.asList(value), fieldType,
											objectFieldType);
						} else {
							throw new IllegalArgumentException(
									"unsupported Type " + typeWrapper
											+ ", must either be subclass of "
											+ "SingularType or MultiType");
						}
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			public void writeDocumentInfoToBean(
					FieldInformation fieldInformation, Document origin,
					Object dest) {
				FrozenField field = fieldInformation.getField();
				BeanField bf = fieldInformation.getBeanField();
				Type typeWrapper;
				try {
					typeWrapper = (Type) bf.type().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				String name = bf.name();
				if (name.equals(Constants.DEFAULT_NAME)) {
					name = field.getName();
				}

				List<Object> values = new ArrayList<Object>();
				if (typeWrapper instanceof SingularFieldType) {
					IndexableField[] indexFields = origin.getFields(name);
					for (IndexableField cur : indexFields) {
						values.add(((SingularFieldType) typeWrapper)
								.toBeanValue(cur));
					}
				} else if (typeWrapper instanceof MultiFieldType) {
					values.addAll(((MultiFieldType) typeWrapper).toBeanValues(
							origin, name));
				} else {
					throw new IllegalArgumentException("unsupported Type "
							+ typeWrapper + ", must either be subclass of "
							+ "SingularType or MultiType");
				}

				if (values.size() > 0) {
					if (values.size() == 1) {
						try {
							field.set(dest, values.get(0));
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					} else {
						LOGGER.log(Level.WARNING,
								"more than one value in an non List/Set field: "
										+ values);
					}
				}
			}

		},
		LIST {

			@Override
			public void writeBeanInfoToDocument(
					FieldInformation fieldInformation, Object origin,
					Document dest) {
				try {
					iterableWriteBeanToDocument(fieldInformation, origin, dest);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void writeDocumentInfoToBean(
					FieldInformation fieldInformation, Document origin,
					Object dest) {
				collectionWriteDocumentToBean(fieldInformation, origin, dest,
						ArrayList.class);
			}

		},
		SET {

			@Override
			public void writeBeanInfoToDocument(
					FieldInformation fieldInformation, Object origin,
					Document dest) {
				try {
					iterableWriteBeanToDocument(fieldInformation, origin, dest);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void writeDocumentInfoToBean(
					FieldInformation fieldInformation, Document origin,
					Object dest) {
				collectionWriteDocumentToBean(fieldInformation, origin, dest,
						HashSet.class);
			}

		};

		private static void iterableWriteBeanToDocument(
				FieldInformation fieldInformation, Object origin, Document dest) {
			FrozenField field = fieldInformation.getField();
			BeanField bf = fieldInformation.getBeanField();
			Class<?> objectFieldType = fieldInformation.getFieldClass();
			FieldType fieldType = fieldInformation.getFieldType();
			Type typeWrapper;
			try {
				typeWrapper = (Type) bf.type().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			String name = bf.name();
			if (name.equals(Constants.DEFAULT_NAME)) {
				name = field.getName();
			}
			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> iterable = (Iterable<Object>) field
						.get(origin);
				if (iterable != null) {
					if (typeWrapper instanceof SingularFieldType) {
						for (Object obj : iterable) {
							((SingularFieldType) typeWrapper)
									.handleDocFieldValue(dest, name, obj,
											fieldType, objectFieldType);
						}
					} else if (typeWrapper instanceof MultiFieldType) {
						((MultiFieldType) typeWrapper).handleDocFieldValues(
								dest, name, iterable, fieldType,
								objectFieldType);
					} else {
						throw new IllegalArgumentException("unsupported Type "
								+ typeWrapper + ", must either be subclass of "
								+ "SingularType or MultiType");
					}
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		private static void collectionWriteDocumentToBean(
				FieldInformation fieldInformation,
				Document origin,
				Object dest,
				@SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass) {
			FrozenField field = fieldInformation.getField();
			BeanField bf = fieldInformation.getBeanField();
			Type typeWrapper;
			try {
				typeWrapper = (Type) bf.type().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			String name = bf.name();
			if (name.equals(Constants.DEFAULT_NAME)) {
				name = field.getName();
			}

			try {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) collectionClass
						.newInstance();
				if (typeWrapper instanceof SingularFieldType) {
					IndexableField[] indexFields = origin.getFields(name);
					for (IndexableField cur : indexFields) {
						collection.add(((SingularFieldType) typeWrapper)
								.toBeanValue(cur));
					}
				} else if (typeWrapper instanceof MultiFieldType) {
					collection.addAll(((MultiFieldType) typeWrapper)
							.toBeanValues(origin, name));
				} else {
					throw new IllegalArgumentException("unsupported Type "
							+ typeWrapper + ", must either be subclass of "
							+ "SingularType or MultiType");
				}
				field.set(dest, collection);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
		}

		public abstract void writeBeanInfoToDocument(
				FieldInformation fieldInformation, Object origin, Document dest);

		public abstract void writeDocumentInfoToBean(
				FieldInformation fieldInformation, Document origin, Object dest);

	}

}
