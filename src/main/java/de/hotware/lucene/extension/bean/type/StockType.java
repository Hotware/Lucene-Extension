package de.hotware.lucene.extension.bean.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

/**
 * Inner classes of this type contain basic Types that are shipped with the
 * extension. Most of the time these types are sufficient <br />
 * <br />
 * All Numbers have two different types: <br />
 * One [Number]Type and one [Number]StringType. <br />
 * The first one tells Lucene to store the Field in a Numeric Field (with the
 * corresponding Lucene NumericType) and the second one converts the Number into
 * a String and then stores that String into the Document. <br />
 * <br />
 * In fact, all Types that don't have a specific name (i.e. SerializeType)
 * convert the field by toString() into the Document and retrieve the value
 * 
 * @author Martin Braun
 */
public final class StockType {

	// TODO: throw Exceptions for the wrong objectFieldType

	private static abstract class BaseStockType extends BaseType {

		public BaseStockType(NumericType numericType) {
			super(numericType);
		}

		@Override
		public void handleDocFieldValue(Document document, String name,
				Object value, FieldType fieldType, Class<?> objectFieldType) {
			Field docField = new Field(name, this.toLuceneValue(value),
					fieldType);
			document.add(docField);
		}

		@Override
		public final Object toBeanValue(IndexableField field) {
			Object value;
			if (this.numericType != null) {
				value = field.numericValue();
			} else if (!(this instanceof SerializeType)) {
				value = field.stringValue();
			} else {
				value = field.binaryValue().bytes;
			}
			if (value == null) {
				throw new IllegalStateException(
						"value was null."
								+ " the index seems to be out of sync with the bean's class!");
			}
			// Strings are allowed to be equal to ""
			if (!(this instanceof StringType) && value.equals("")) {
				return null;
			}
			return this.toBeanValueInternal(value);
		}

		protected Object toBeanValueInternal(Object value) {
			return value;
		}

		protected String toLuceneValue(Object value) {
			if (value == null) {
				return "";
			}
			return value.toString();
		}

	}

	public static class DoubleType extends BaseStockType {

		public DoubleType() {
			super(NumericType.DOUBLE);
		}

		@Override
		public void handleDocFieldValue(Document document, String name,
				Object value, FieldType fieldType, Class<?> objectFieldType) {
			DoubleField docField = new DoubleField(name, (Double) value,
					fieldType);
			document.add(docField);
		}

	}

	public static class DoubleStringType extends BaseStockType {

		public DoubleStringType() {
			super(null);
		}

		@Override
		public Object toBeanValueInternal(Object value) {
			return Double.parseDouble((String) value);
		}

	}

	public static class FloatType extends BaseStockType {

		public FloatType() {
			super(NumericType.FLOAT);
		}

		@Override
		public void handleDocFieldValue(Document document, String name,
				Object value, FieldType fieldType, Class<?> objectFieldType) {
			FloatField docField = new FloatField(name, (Float) value, fieldType);
			document.add(docField);
		}

	}

	public static class FloatStringType extends BaseStockType {

		public FloatStringType() {
			super(null);
		}

		@Override
		public Object toBeanValueInternal(Object value) {
			return Float.parseFloat((String) value);
		}

	}

	public static class IntegerType extends BaseStockType {

		public IntegerType() {
			super(NumericType.INT);
		}

		@Override
		public void handleDocFieldValue(Document document, String name,
				Object value, FieldType fieldType, Class<?> objectFieldType) {
			IntField docField = new IntField(name, (Integer) value, fieldType);
			document.add(docField);
		}

	}

	public static class IntegerStringType extends BaseStockType {

		public IntegerStringType() {
			super(null);
		}

		@Override
		public Object toBeanValueInternal(Object value) {
			return Integer.parseInt((String) value);
		}

	}

	public static class LongType extends BaseStockType {

		public LongType() {
			super(NumericType.LONG);
		}

		@Override
		public void handleDocFieldValue(Document document, String name,
				Object value, FieldType fieldType, Class<?> objectFieldType) {
			LongField docField = new LongField(name, (Long) value, fieldType);
			document.add(docField);
		}

	}

	public static class LongStringType extends BaseStockType {

		public LongStringType() {
			super(null);
		}

		@Override
		public Object toBeanValueInternal(Object value) {
			return Long.parseLong((String) value);
		}

	}

	public static class StringType extends BaseStockType {

		public StringType() {
			super(null);
		}

	}

	/**
	 * additionally stores the TermVector's Positions and Offsets
	 * 
	 * @author Martin Braun
	 */
	public static class StringTermVectorPositionsOffsetsType extends
			BaseStockType {

		public StringTermVectorPositionsOffsetsType() {
			super(null);
		}

		@Override
		public void configureFieldType(FieldType fieldType) {
			super.configureFieldType(fieldType);
			fieldType.setStoreTermVectors(true);
			fieldType.setStoreTermVectorPositions(true);
			fieldType.setStoreTermVectorOffsets(true);
		}

	}

	public static class BooleanType extends BaseStockType {

		public BooleanType() {
			super(null);
		}

		@Override
		public Object toBeanValueInternal(Object value) {
			return Boolean.parseBoolean((String) value);
		}

	}

	/**
	 * the index attribute is ignored, as these Serialized Types can only be
	 * stored to be retrieved afterwards. <br />
	 * <br />
	 * This class does serialization via {@link java.io.ByteArrayOutputStream}'s
	 * writeObject so all types that should be serialized here, have to
	 * implement the {@link java.io.Serializable} interface. <br />
	 * <br />
	 * This method of serialization is known not to be the fastest way,
	 * so if you need better performance it might be a good idea to implement
	 * your own {@link Type}
	 * 
	 * @author Martin Braun
	 */
	public static class SerializeType extends BaseStockType {

		public SerializeType() {
			super(null);
		}

		@Override
		protected Object toBeanValueInternal(Object value) {
			ObjectInputStream in = null;
			try {
				ByteArrayInputStream bas = new ByteArrayInputStream(
						(byte[]) value);
				in = new ObjectInputStream(bas);
				return in.readObject();
			} catch (ClassNotFoundException e) {
				throw new AssertionError();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		public void handleDocFieldValue(Document document, String name,
				Object value, FieldType fieldType, Class<?> objectFieldType) {
			StoredField docField = new StoredField(name,
					toSerializedLuceneValue(value));
			document.add(docField);
		}

	}

	private static byte[] toSerializedLuceneValue(Object value) {
		ObjectOutputStream out = null;
		try {
			ByteArrayOutputStream serData = new ByteArrayOutputStream();
			out = new ObjectOutputStream(serData);
			out.writeObject(value);
			return serData.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
