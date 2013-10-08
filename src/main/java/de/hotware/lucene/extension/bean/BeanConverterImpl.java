package de.hotware.lucene.extension.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * TODO: toBeanValue(...): maybe change this to the specific value-methods (to prevent all the parsing)
 * 
 * @author Martin Braun
 */
public class BeanConverterImpl implements BeanConverter {
	
	private final static Logger LOGGER = Logger.getLogger(BeanConverterImpl.class.getName());

	private static final Set<Class<?>> PLAIN_TYPES;
	static {
		{
			Set<Class<?>> tmp = new HashSet<Class<?>>();
			//all primitives and their wrappers
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
			//and strings
			tmp.add(String.class);
			PLAIN_TYPES = Collections.unmodifiableSet(tmp);
		}
	}
	
	private static final Set<Class<?>> COLLECTION_TYPES;
	static {
		{
			Set<Class<?>> tmp = new HashSet<Class<?>>();
			//and one dimensional collections
			tmp.add(List.class);
			tmp.add(Set.class);
			//and maps
			//TODO: maybe add this feature later.
			//too time consuming and many changes in 
			//the other classes have to be made
			//tmp.add(Map.class);
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
			for(Class<?> val : ALL_TYPES) {
				tmp.put(val, TypeHandler.DEFAULT);
			}
			tmp.put(List.class, TypeHandler.LIST);
			tmp.put(Set.class, TypeHandler.SET);
			//TODO: maybe add this feature later.
			//too time consuming and many changes in 
			//the other classes have to be made
			//tmp.put(Map.class, TypeHandler.MAP);
			TYPE_HANDLER = Collections.unmodifiableMap(tmp);
		}
	}
	
	private static enum TypeHandler {
		DEFAULT {
			
			@Override
			public void writeBeanInfoToDocument(FieldInformation fieldInformation, Object origin, Document dest) {
				Field field = fieldInformation.getField();
				BeanField bf = fieldInformation.getBeanField();
				Class<?> objectFieldType = fieldInformation.getFieldClass();
				FieldType fieldType = fieldInformation.getFieldType();
				TypeWrapper typeWrapper = bf.type();
				String name = bf.name();
				if(name.equals(Constants.DEFAULT_NAME)) {
					name = field.getName();
				}
				
				try {
					Object value = field.get(origin);
					if(value != null) {
						typeWrapper.handleDocFieldValue(dest,
								name,
								field.get(origin),
								fieldType,
								objectFieldType);
					}
				} catch(IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch(IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			public void writeDocumentInfoToBean(FieldInformation fieldInformation, Document origin, Object dest) {
				Field field = fieldInformation.getField();
				BeanField bf = fieldInformation.getBeanField();
				TypeWrapper typeWrapper = bf.type();
				String name = bf.name();
				if(name.equals(Constants.DEFAULT_NAME)) {
					name = field.getName();
				}
				
				IndexableField[] indexFields = origin.getFields(name);
				List<Object> values = new ArrayList<Object>();
				for(IndexableField cur : indexFields) {
					values.add(typeWrapper.toBeanValue(cur));
				}
				if(values.size() > 0) {
					if(values.size() == 1)
						try {
							field.set(dest, values.get(0));
						} catch(IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch(IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					} else {
						LOGGER.log(Level.WARNING, "more than one value in an non List/Set field.");
					}
				}
			
		}, LIST {
			
			@Override
			public void writeBeanInfoToDocument(FieldInformation fieldInformation, Object origin, Document dest) {		
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
				collectionWriteDocumentToBean(fieldInformation, origin, dest, ArrayList.class);
			}
			
		}, SET {
			
			@Override
			public void writeBeanInfoToDocument(FieldInformation fieldInformation, Object origin, Document dest) {		
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
				collectionWriteDocumentToBean(fieldInformation, origin, dest, HashSet.class);
			}
			
		};
		
		private static void iterableWriteBeanToDocument(FieldInformation fieldInformation, Object origin, Document dest) {
			Field field = fieldInformation.getField();
			BeanField bf = fieldInformation.getBeanField();
			Class<?> objectFieldType = fieldInformation.getFieldClass();
			FieldType fieldType = fieldInformation.getFieldType();
			TypeWrapper typeWrapper = bf.type();
			String name = bf.name();
			if(name.equals(Constants.DEFAULT_NAME)) {
				name = field.getName();
			}
			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> iterable = (Iterable<Object>) field.get(origin);
				if(iterable != null) {
					for(Object obj : iterable) {
						typeWrapper.handleDocFieldValue(dest,
								name,
								obj,
								fieldType,
								objectFieldType);
					}
				}
			} catch(IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		private static void collectionWriteDocumentToBean(FieldInformation fieldInformation, Document origin,
				Object dest, @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass) {
			Field field = fieldInformation.getField();
			BeanField bf = fieldInformation.getBeanField();
			TypeWrapper typeWrapper = bf.type();
			String name = bf.name();
			if(name.equals(Constants.DEFAULT_NAME)) {
				name = field.getName();
			}

			try {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) collectionClass.newInstance();
				IndexableField[] indexFields = origin.getFields(name);
				for(IndexableField cur : indexFields) {
					collection.add(typeWrapper.toBeanValue(cur));
				}
				field.set(dest, collection);
			} catch(IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
		}
//		},
//		MAP {
//			@Override
//			public void writeBeanInfoToDocument() {
//				//  Auto-generated method stub
//				
//			}
//
//			@Override
//			public void writeDocumentInfoToBean(FieldInformation fieldInformation,
//					Document origin, Object dest) {
//				List<Type> genericTypeArgs = fieldInformation.getGenericTypeArgs();
//				Field field = fieldInformation.getField();
//				BeanField bf = fieldInformation.getBeanField();
//				Class<?> objectFieldClass = fieldInformation.getFieldClass();
//				TypeWrapper typeWrapper = bf.type();
//				String name = bf.name();
//				if(name.equals(Constants.DEFAULT_NAME)) {
//					name = field.getName();
//				}
//				IndexableField[] lookupFields = origin.getFields(name + "_lookup");
//				for(IndexableField lookup : lookupFields) {
//					StringField stringField = ((StringField) lookup);
//					String key = stringField.stringValue();
//					IndexableField[] indexFields = origin.getFields(key);
//				}
//				IndexableField[] indexFields = origin.getFields(name);
//				List<Object> values = new ArrayList<Object>();
//				for(IndexableField cur : indexFields) {
//					//: maybe change this to the specific value-methods (to prevent all the parsing)
//					values.add(typeWrapper.toBeanValue(cur));
//				}
//			}
		
		public abstract void writeBeanInfoToDocument(FieldInformation fieldInformation, Object origin, Document dest);
		public abstract void writeDocumentInfoToBean(FieldInformation fieldInformation, Document origin, Object dest);		
		
		
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
			BeanField bf = fieldInformation.getBeanField();
			Class<?> objectFieldClass = fieldInformation.getFieldClass();
			TypeWrapper typeWrapper = bf.type();
			if(typeWrapper == null) {
				throw new IllegalArgumentException("typeWrapper may not be null");
			}
			if(typeWrapper != TypeWrapper.SERIALIZED && !ALL_TYPES.contains(objectFieldClass)) {
				throw new IllegalArgumentException("type of Java-Bean field not supported");
			}
			TypeHandler typeHandler = TYPE_HANDLER.get(objectFieldClass);
			if(typeHandler == null) {
				if(typeWrapper == TypeWrapper.SERIALIZED) {
					//serialisation is handled like a default object
					typeHandler = TypeHandler.DEFAULT;
				} else {
					throw new AssertionError("typeHandler was null at a point where the typeHandler"
							+ " may only be null if typeWrapper is SERIALIZED");
				}
			}
			typeHandler.writeDocumentInfoToBean(fieldInformation, document, ret);	
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
			BeanField bf = fieldInformation.getBeanField();
			Class<?> objectFieldType = fieldInformation.getFieldClass();
			Class<?> objectFieldClass = fieldInformation.getFieldClass();
			TypeWrapper typeWrapper = bf.type();
			if(typeWrapper != TypeWrapper.SERIALIZED &&!ALL_TYPES.contains(objectFieldType)) {
				throw new IllegalArgumentException("only primitive types and "
						+ "Lists/Sets of them are allowed");
			}
			TypeHandler typeHandler = TYPE_HANDLER.get(objectFieldClass);
			if(typeHandler == null) {
				if(typeWrapper == TypeWrapper.SERIALIZED) {
					//serialisation is handled like a default object
					typeHandler = TypeHandler.DEFAULT;
				} else {
					throw new AssertionError("typeHandler was null at a point where the typeHandler"
							+ " may only be null if typeWrapper is SERIALIZED");
				}
			}
			typeHandler.writeBeanInfoToDocument(fieldInformation, bean, ret);	
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

	@Override
	public String toString() {
		return "BeanConverterImpl [cache=" + cache + "]";
	}

}
