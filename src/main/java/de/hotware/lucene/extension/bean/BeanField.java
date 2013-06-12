package de.hotware.lucene.extension.bean;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.reflect.Array;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.Version;

/**
 * Utility class used for annotation of Beans that should be stored into lucene
 * Use this class in addition with a BeanConverter Implementation
 * 
 * @author Martin Braun
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface BeanField {

	public static final String DEFAULT_NAME = "#DEFAULT";
	public static final AnalyzerWrapper DEFAULT_ANALYZER = (AnalyzerWrapper) Util
			.getDefaultValueForAnnotationMethod(BeanField.class, "analyzer");

	public String name() default DEFAULT_NAME;

	public boolean index() default false;

	public boolean store() default true;

	public TypeWrapper type();

	public AnalyzerWrapper analyzer() default AnalyzerWrapper.STANDARD_ANALYZER;

	public static enum TypeWrapper {
		DOUBLE(NumericType.DOUBLE) {

			protected void handleDocFieldValue(Document document,
					String name,
					Object value,
					FieldType fieldType,
					Class<?> objectFieldType) {
				DoubleField docField = new DoubleField(name,
						(Double) value,
						fieldType);
				document.add(docField);
			}

		},
		DOUBLE_STRING(null) {

			@Override
			public Object toBeanValueInternal(Object value) {
				return Double.parseDouble((String) value);
			}

		},
		FLOAT(NumericType.FLOAT) {

			protected void handleDocFieldValue(Document document,
					String name,
					Object value,
					FieldType fieldType,
					Class<?> objectFieldType) {
				FloatField docField = new FloatField(name,
						(Float) value,
						fieldType);
				document.add(docField);
			}

		},
		FLOAT_STRING(null) {

			@Override
			public Object toBeanValueInternal(Object value) {
				return Float.parseFloat((String) value);
			}

		},
		INTEGER(NumericType.INT) {

			protected void handleDocFieldValue(Document document,
					String name,
					Object value,
					FieldType fieldType,
					Class<?> objectFieldType) {
				IntField docField = new IntField(name,
						(Integer) value,
						fieldType);
				document.add(docField);
			}

		},
		INTEGER_STRING(null) {

			@Override
			public Object toBeanValueInternal(Object value) {
				return Integer.parseInt((String) value);
			}

		},
		LONG(NumericType.LONG) {

			protected void handleDocFieldValue(Document document,
					String name,
					Object value,
					FieldType fieldType,
					Class<?> objectFieldType) {
				LongField docField = new LongField(name,
						(Long) value,
						fieldType);
				document.add(docField);
			}

		},
		STRING(null),
		BOOLEAN(null) {

			@Override
			public Object toBeanValueInternal(Object value) {
				return Boolean.parseBoolean((String) value);
			}

		},
		SERIALIZED(null) {

			@Override
			protected Object toBeanValueInternal(Object value) {
				ObjectInputStream in = null;
				try {
					ByteArrayInputStream bas = new ByteArrayInputStream((byte[]) value);
					in = new ObjectInputStream(bas);
					return in.readObject();
				} catch(ClassNotFoundException e) {
					throw new AssertionError();
				} catch(IOException e) {
					throw new RuntimeException(e);
				} finally {
					if(in != null) {
						try {
							in.close();
						} catch(IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}

			protected void handleDocFieldValue(Document document,
					String name,
					Object value,
					FieldType fieldType,
					Class<?> objectFieldType) {
				StoredField docField = new StoredField(name,
						toSerializedLuceneValue(value));
				document.add(docField);
			}

		};

		private final NumericType numericType;

		private TypeWrapper(NumericType numericType) {
			this.numericType = numericType;
		}

		public NumericType getNumericType() {
			return this.numericType;
		}

		public final void addDocFields(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			if(objectFieldType.isArray()) {
				if(value != null) {
					int size = Array.getLength(value);
					for(int i = 0; i < size; ++i) {
						this.handleDocFieldValue(document,
								name,
								Array.get(value, i),
								fieldType,
								objectFieldType);
					}
				}
			} else {
				if(value != null) {
					this.handleDocFieldValue(document,
							name,
							value,
							fieldType,
							objectFieldType);
				}
			}
		}

		public final Object toBeanValue(IndexableField field) {
			Object value;
			if(this.numericType != null) {
				value = field.numericValue();
			} else if(this != TypeWrapper.SERIALIZED) {
				value = field.stringValue();
			} else {
				value = field.binaryValue().bytes;
			}
			if(value == null) {
				throw new IllegalStateException("value was null."
						+ " the index seems to be out of sync with the bean's class!");
			}
			//Strings are allowed to be equal to ""
			if(this != TypeWrapper.STRING && value.equals("")) {
				return null;
			}
			return this.toBeanValueInternal(value);
		}

		protected Object toBeanValueInternal(Object value) {
			return value;
		}

		protected void handleDocFieldValue(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			Field docField = new Field(name,
					this.toLuceneValue(value),
					fieldType);
			document.add(docField);
		}

		protected String toLuceneValue(Object value) {
			if(value == null) {
				return "";
			}
			return value.toString();
		}

		protected static byte[] toSerializedLuceneValue(Object value) {
			ObjectOutputStream out = null;
			try {
				ByteArrayOutputStream serData = new ByteArrayOutputStream();
				out = new ObjectOutputStream(serData);
				out.writeObject(value);
				return serData.toByteArray();
			} catch(IOException e) {
				throw new RuntimeException(e);
			} finally {
				if(out != null) {
					try {
						out.close();
					} catch(IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

	}

	public enum AnalyzerWrapper {
		KEY_WORD_ANALYZER(new KeywordAnalyzer()),
		LOCALE_ANALYZER(null) {

			@Override
			public Analyzer getAnalyzer(String locale) {
				return LocaleAnalyzer.getAnalyzer(locale);
			}

		},
		GERMAN_ANALYZER(new GermanAnalyzer(Version.LUCENE_43)),
		ENGLISH_ANALYZER(new EnglishAnalyzer(Version.LUCENE_43)),
		STANDARD_ANALYZER(new StandardAnalyzer(Version.LUCENE_43));

		private final Analyzer analyzer;

		private AnalyzerWrapper(Analyzer analyzer) {
			this.analyzer = analyzer;
		}

		public Analyzer getAnalyzer(String locale) {
			return this.analyzer;
		}

	}

}
