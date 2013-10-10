package de.hotware.lucene.extension.bean.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

public abstract class StockType implements Type {
	
	private final NumericType numericType;

	public StockType(NumericType numericType) {
		this.numericType = numericType;
	}

	public NumericType getNumericType() {
		return this.numericType;
	}

	public final Object toBeanValue(IndexableField field) {
		Object value;
		if(this.numericType != null) {
			value = field.numericValue();
		} else if(!(this instanceof SerializeType)) {
			value = field.stringValue();
		} else {
			value = field.binaryValue().bytes;
		}
		if(value == null) {
			throw new IllegalStateException("value was null."
					+ " the index seems to be out of sync with the bean's class!");
		}
		//Strings are allowed to be equal to ""
		if(!(this instanceof StringType) && value.equals("")) {
			return null;
		}
		return this.toBeanValueInternal(value);
	}

	protected Object toBeanValueInternal(Object value) {
		return value;
	}

	public void handleDocFieldValue(Document document,
			String name,
			Object value,
			FieldType fieldType,
			Class<?> objectFieldType) {
		Field docField = new Field(name, this.toLuceneValue(value), fieldType);
		document.add(docField);
	}

	protected String toLuceneValue(Object value) {
		if(value == null) {
			return "";
		}
		return value.toString();
	}
	
	@Override
	public void configureFieldType(FieldType fieldType) {
		fieldType.setNumericType(this.getNumericType());
	}
	
	public static class DoubleType extends StockType {

		public DoubleType() {
			super(NumericType.DOUBLE);
		}
		
		public void handleDocFieldValue(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			DoubleField docField = new DoubleField(name,
					(Double) value,
					fieldType);
			document.add(docField);
		}
		
	}
	
	public static class DoubleStringType extends StockType {

		public DoubleStringType() {
			super(null);
		}
		
		@Override
		public Object toBeanValueInternal(Object value) {
			return Double.parseDouble((String) value);
		}
		
	}
	
	public static class FloatType extends StockType {

		public FloatType() {
			super(NumericType.FLOAT);
		}
		
		public void handleDocFieldValue(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			FloatField docField = new FloatField(name, (Float) value, fieldType);
			document.add(docField);
		}
		
	}
	
	public static class FloatStringType extends StockType {

		public FloatStringType() {
			super(null);
		}
		
		@Override
		public Object toBeanValueInternal(Object value) {
			return Float.parseFloat((String) value);
		}
		
	}
	
	public static class IntegerType extends StockType {

		public IntegerType() {
			super(NumericType.INT);
		}
		
		public void handleDocFieldValue(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			IntField docField = new IntField(name, (Integer) value, fieldType);
			document.add(docField);
		}
		
	}
	
	public static class IntegerStringType extends StockType {
		
		public IntegerStringType() {
			super(null);
		}

		@Override
		public Object toBeanValueInternal(Object value) {
			return Integer.parseInt((String) value);
		}
		
	}

	public static class LongType extends StockType {

		public LongType() {
			super(NumericType.LONG);
		}
		
		public void handleDocFieldValue(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			LongField docField = new LongField(name, (Long) value, fieldType);
			document.add(docField);
		}
		
	}
	
	public static class LongStringType extends StockType {

		public LongStringType() {
			super(null);
		}
		
		@Override
		public Object toBeanValueInternal(Object value) {
			return Long.parseLong((String) value);
		}
		
	}
	
	public static class StringType extends StockType {

		public StringType() {
			super(null);
		}
		
	}
	

	public static class BooleanType extends StockType {
		
		public BooleanType() {
			super(null);
		}
		
		@Override
		public Object toBeanValueInternal(Object value) {
			return Boolean.parseBoolean((String) value);
		}
		
	}
	
	public static class SerializeType extends StockType {
		
		public SerializeType() {
			super(null);
		}
		
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

		public void handleDocFieldValue(Document document,
				String name,
				Object value,
				FieldType fieldType,
				Class<?> objectFieldType) {
			StoredField docField = new StoredField(name,
					toSerializedLuceneValue(value));
			document.add(docField);
		}
		
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
