package de.hotware.lucene.extension.bean.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;

import de.hotware.lucene.extension.bean.BeanConverter;
import de.hotware.lucene.extension.bean.BeanConverterImpl;
import de.hotware.lucene.extension.bean.BeanField;
import de.hotware.lucene.extension.bean.BeanField.AnalyzerWrapper;
import de.hotware.lucene.extension.bean.BeanInformationCacheImpl;
import de.hotware.lucene.extension.bean.BeanField.TypeWrapper;
import junit.framework.TestCase;

public class BeanConverterTest extends TestCase {
	
	public static class TestBean {
		
		@BeanField(store = true, index = true, type = TypeWrapper.INTEGER_STRING)
		public Integer integerStringTest;
		@BeanField(store = true, index = true, type = TypeWrapper.INTEGER)
		public Integer integerTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.INTEGER_STRING)
		public int integerPrimStringTest;
		@BeanField(store = true, index = true, type = TypeWrapper.INTEGER)
		public int integerPrimTest;

		@BeanField(store = true, index = true, type = TypeWrapper.LONG_STRING)
		public Long longStringTest;
		@BeanField(store = true, index = true, type = TypeWrapper.LONG)
		public Long longTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.LONG_STRING)
		public long longStringPrimTest;
		@BeanField(store = true, index = true, type = TypeWrapper.LONG)
		public long longPrimTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.FLOAT)
		public Float floatTest;
		@BeanField(store = true, index = true, type = TypeWrapper.FLOAT_STRING)
		public Float floatStringTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.FLOAT)
		public float floatPrimTest;
		@BeanField(store = true, index = true, type = TypeWrapper.FLOAT_STRING)
		public float floatStringPrimTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.DOUBLE)
		public Double doubleTest;
		@BeanField(store = true, index = true, type = TypeWrapper.DOUBLE_STRING)
		public Double doubleStringTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.DOUBLE)
		public double doublePrimTest;
		@BeanField(store = true, index = true, type = TypeWrapper.DOUBLE_STRING)
		public double doubleStringPrimTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.BOOLEAN)
		public Boolean booleanTest;
		@BeanField(store = true, index = true, type = TypeWrapper.BOOLEAN)
		public boolean booleanPrimTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.STRING)
		public String stringTest;
		
		@BeanField(store = false, index = true, type = TypeWrapper.STRING)
		public String notStoredButIndexedTest;
		@BeanField(store = true, index = false, type = TypeWrapper.STRING)
		public String notIndexedButStoredTest;
		
		@BeanField(store = false, index = true, type = TypeWrapper.STRING, analyzer = AnalyzerWrapper.KEY_WORD_ANALYZER)
		public String customAnalyzerTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.STRING)
		public List<String> listTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.STRING)
		public Set<String> setTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.STRING)
		public Set<String> emptySetTest;
		
		//index is ignored, so do whatever you want here
		@BeanField(store = true, index = false, type = TypeWrapper.SERIALIZED)
		public Object serializeTest;
		
		@BeanField(store = true, index = true, type = TypeWrapper.STRING, name = "customName")
		public String customNameTest;
		
		public String notAnnotatedTest;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (booleanPrimTest ? 1231 : 1237);
			result = prime * result
					+ ((booleanTest == null) ? 0 : booleanTest.hashCode());
			result = prime
					* result
					+ ((customAnalyzerTest == null) ? 0 : customAnalyzerTest
							.hashCode());
			result = prime
					* result
					+ ((customNameTest == null) ? 0 : customNameTest.hashCode());
			long temp;
			temp = Double.doubleToLongBits(doublePrimTest);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(doubleStringPrimTest);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime
					* result
					+ ((doubleStringTest == null) ? 0 : doubleStringTest
							.hashCode());
			result = prime * result
					+ ((doubleTest == null) ? 0 : doubleTest.hashCode());
			result = prime * result + Float.floatToIntBits(floatPrimTest);
			result = prime * result + Float.floatToIntBits(floatStringPrimTest);
			result = prime
					* result
					+ ((floatStringTest == null) ? 0 : floatStringTest
							.hashCode());
			result = prime * result
					+ ((floatTest == null) ? 0 : floatTest.hashCode());
			result = prime * result + integerPrimStringTest;
			result = prime * result + integerPrimTest;
			result = prime
					* result
					+ ((integerStringTest == null) ? 0 : integerStringTest
							.hashCode());
			result = prime * result
					+ ((integerTest == null) ? 0 : integerTest.hashCode());
			result = prime * result
					+ ((listTest == null) ? 0 : listTest.hashCode());
			result = prime * result
					+ (int) (longPrimTest ^ (longPrimTest >>> 32));
			result = prime * result
					+ (int) (longStringPrimTest ^ (longStringPrimTest >>> 32));
			result = prime
					* result
					+ ((longStringTest == null) ? 0 : longStringTest.hashCode());
			result = prime * result
					+ ((longTest == null) ? 0 : longTest.hashCode());
			result = prime
					* result
					+ ((notAnnotatedTest == null) ? 0 : notAnnotatedTest
							.hashCode());
			result = prime
					* result
					+ ((notIndexedButStoredTest == null) ? 0
							: notIndexedButStoredTest.hashCode());
			result = prime
					* result
					+ ((notStoredButIndexedTest == null) ? 0
							: notStoredButIndexedTest.hashCode());
			result = prime * result
					+ ((serializeTest == null) ? 0 : serializeTest.hashCode());
			result = prime * result
					+ ((setTest == null) ? 0 : setTest.hashCode());
			result = prime * result
					+ ((stringTest == null) ? 0 : stringTest.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestBean other = (TestBean) obj;
			if (booleanPrimTest != other.booleanPrimTest)
				return false;
			if (booleanTest == null) {
				if (other.booleanTest != null)
					return false;
			} else if (!booleanTest.equals(other.booleanTest))
				return false;
			if (customAnalyzerTest == null) {
				if (other.customAnalyzerTest != null)
					return false;
			} else if (!customAnalyzerTest.equals(other.customAnalyzerTest))
				return false;
			if (customNameTest == null) {
				if (other.customNameTest != null)
					return false;
			} else if (!customNameTest.equals(other.customNameTest))
				return false;
			if (Double.doubleToLongBits(doublePrimTest) != Double
					.doubleToLongBits(other.doublePrimTest))
				return false;
			if (Double.doubleToLongBits(doubleStringPrimTest) != Double
					.doubleToLongBits(other.doubleStringPrimTest))
				return false;
			if (doubleStringTest == null) {
				if (other.doubleStringTest != null)
					return false;
			} else if (!doubleStringTest.equals(other.doubleStringTest))
				return false;
			if (doubleTest == null) {
				if (other.doubleTest != null)
					return false;
			} else if (!doubleTest.equals(other.doubleTest))
				return false;
			if (Float.floatToIntBits(floatPrimTest) != Float
					.floatToIntBits(other.floatPrimTest))
				return false;
			if (Float.floatToIntBits(floatStringPrimTest) != Float
					.floatToIntBits(other.floatStringPrimTest))
				return false;
			if (floatStringTest == null) {
				if (other.floatStringTest != null)
					return false;
			} else if (!floatStringTest.equals(other.floatStringTest))
				return false;
			if (floatTest == null) {
				if (other.floatTest != null)
					return false;
			} else if (!floatTest.equals(other.floatTest))
				return false;
			if (integerPrimStringTest != other.integerPrimStringTest)
				return false;
			if (integerPrimTest != other.integerPrimTest)
				return false;
			if (integerStringTest == null) {
				if (other.integerStringTest != null)
					return false;
			} else if (!integerStringTest.equals(other.integerStringTest))
				return false;
			if (integerTest == null) {
				if (other.integerTest != null)
					return false;
			} else if (!integerTest.equals(other.integerTest))
				return false;
			if (listTest == null) {
				if (other.listTest != null)
					return false;
			} else if (!listTest.equals(other.listTest))
				return false;
			if (longPrimTest != other.longPrimTest)
				return false;
			if (longStringPrimTest != other.longStringPrimTest)
				return false;
			if (longStringTest == null) {
				if (other.longStringTest != null)
					return false;
			} else if (!longStringTest.equals(other.longStringTest))
				return false;
			if (longTest == null) {
				if (other.longTest != null)
					return false;
			} else if (!longTest.equals(other.longTest))
				return false;
			if (notAnnotatedTest == null) {
				if (other.notAnnotatedTest != null)
					return false;
			} else if (!notAnnotatedTest.equals(other.notAnnotatedTest))
				return false;
			if (notIndexedButStoredTest == null) {
				if (other.notIndexedButStoredTest != null)
					return false;
			} else if (!notIndexedButStoredTest
					.equals(other.notIndexedButStoredTest))
				return false;
			if (notStoredButIndexedTest == null) {
				if (other.notStoredButIndexedTest != null)
					return false;
			} else if (!notStoredButIndexedTest
					.equals(other.notStoredButIndexedTest))
				return false;
			if (serializeTest == null) {
				if (other.serializeTest != null)
					return false;
			} else if (!serializeTest.equals(other.serializeTest))
				return false;
			if (setTest == null) {
				if (other.setTest != null)
					return false;
			} else if (!setTest.equals(other.setTest))
				return false;
			if (stringTest == null) {
				if (other.stringTest != null)
					return false;
			} else if (!stringTest.equals(other.stringTest))
				return false;
			return true;
		}
		
	}
	
	public final class WrongTypeTest {
		
		//not being serialized -> exception is expected when used with the BeanConverter
		@BeanField(store = true, index = true, type = TypeWrapper.STRING)
		public Object wrongType;
		
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBeanConverterImpl() {
		new BeanConverterImpl(new BeanInformationCacheImpl());
	}

	public void testBeanDocumentConversionViceVersa() throws IllegalArgumentException, IllegalAccessException {
		BeanConverter converter = new BeanConverterImpl(new BeanInformationCacheImpl());
		Field[] fields = TestBean.class.getFields();
		TestBean testBean = new TestBean();
		for(Field field : fields) {
			String fieldName = field.getName();
			Class<?> type = field.getType();
			if(type.equals(int.class)) {
				field.setInt(testBean, Integer.MAX_VALUE);
			} else if(type.equals(long.class)) {
				field.setLong(testBean, Long.MAX_VALUE);
			} else if(type.equals(double.class)) {
				field.setDouble(testBean, Double.MAX_VALUE);
			} else if(type.equals(float.class)) {
				field.setFloat(testBean, Float.MAX_VALUE);
			} else if(type.equals(boolean.class)) {
				field.setBoolean(testBean, true);
			} else if(type.equals(Integer.class)) {
				field.set(testBean, Integer.MAX_VALUE);
			} else if(type.equals(Long.class)) {
				field.set(testBean, Long.MAX_VALUE);
			} else if(type.equals(Double.class)) {
				field.set(testBean, Double.MAX_VALUE);
			} else if(type.equals(Float.class)) {
				field.set(testBean, Float.MAX_VALUE);
			} else if(type.equals(Boolean.class)) {
				field.set(testBean, true);
			} else if(type.equals(String.class)) {	
				field.set(testBean, "Test");
			} else if(fieldName.equals("emptySetTest")) {
				field.set(testBean, new HashSet<String>());
			} else if(type.equals(Set.class) ) {
				Set<String> set = new HashSet<String>();
				set.add("1");
				set.add("2");
				set.add("3");
				field.set(testBean, set);
			} else if(type.equals(List.class)) {
				List<String> list = new ArrayList<String>();
				list.add("1");
				list.add("2");
				list.add("3");
				field.set(testBean, list);
			} else if(type.equals(Object.class)) {
				field.set(testBean, new Date());
			} else {
				fail("type is not handled in the Unit-Test, please add " + type);
			}
			Document document = converter.beanToDocument(testBean);
			//check if all values are stored the same way they were entered
			if(fieldName.equals("serializeTest")) {
				System.out.println("doing serialize equality test.");
				assertTrue(Arrays.equals(toSerializedLuceneValue(field.get(testBean)), 
						document.getBinaryValue(fieldName).bytes));
			} else if(fieldName.equals("customNameTest")) {
				System.out.println("doing custom name equality test.");
				String originalValue = (String) field.get(testBean);
				String documentValue = document.get("customName");
				assertEquals(originalValue, documentValue);
			} else if(fieldName.equals("notAnnotatedTest")) {
				System.out.println("doing not annotated test.");
				assertEquals(null, document.get(fieldName));
			} else if(fieldName.equals("listTest")) {
				System.out.println("doing listTest");
				@SuppressWarnings("unchecked")
				List<String> originalList = (List<String>) field.get(testBean);
				IndexableField[] documentFields = document.getFields(fieldName);
				for(int i = 0; i < originalList.size(); ++i) {
					assertEquals(originalList.get(i), documentFields[i].stringValue());
				}
			} else if(fieldName.equals("setTest")) {
				System.out.println("doing listTest");
				@SuppressWarnings("unchecked")
				Set<String> originalSet = (Set<String>) field.get(testBean);
				Set<String> docSet = new HashSet<String>();
				for(IndexableField documentField : document.getFields(fieldName)) {
					docSet.add(documentField.stringValue());
				}
				assertEquals(originalSet, docSet);
			} else if(fieldName.equals("emptySetTest")) {
				System.out.println("doing emptySetTest");
				assertEquals(null, document.get(fieldName));
			} else {
				//normally a check is needed, but in the test-case we
				//can do this without checking for a present annotation
				BeanField bf = field.getAnnotation(BeanField.class);
				System.out.println("doing " + bf.type() +" tests on \"" +  fieldName + "\".");
				assertEquals(field.get(testBean).toString(), document.get(fieldName));
				IndexableField indexField = document.getField(fieldName);
				IndexableFieldType indexFieldType = indexField.fieldType();
				assertEquals(bf.store(), indexFieldType.stored());
				assertEquals(bf.index(), indexFieldType.indexed());
				assertEquals(bf.tokenized(), indexFieldType.tokenized());
				//TODO: test if fieldType is correct?
			}
		}
		
		//now that all the conversion works we can safely generate
		//a document with that and work backwards :)
		System.out.println("doing reverse conversion (document to bean) test.");
		Document document = converter.beanToDocument(testBean);
		TestBean reverseBean = converter.documentToBean(TestBean.class, document);
		
		//setting the stuff that can not be in the document and therefore not in the reverseBean
		reverseBean.notAnnotatedTest = testBean.notAnnotatedTest;
		reverseBean.notStoredButIndexedTest = testBean.notStoredButIndexedTest;
		assertTrue(testBean.equals(reverseBean));		
		
		System.out.println("Result: conversion test successfull.");
	}
	
	public void testWrongType() {
		System.out.println("doing malformed bean tests.");
		BeanConverter converter = new BeanConverterImpl(new BeanInformationCacheImpl());
		try {
			WrongTypeTest wrongTypeTest = new WrongTypeTest();
			//really awkward type. :)
			wrongTypeTest.wrongType = new HashMap<Object, GregorianCalendar>();
			converter.beanToDocument(wrongTypeTest);
			fail("Exception expected");
		} catch(IllegalArgumentException e) {
		}
		try {
			converter.beanToDocument(WrongTypeTest.class);
			fail("Exception expected");
		} catch(IllegalArgumentException e) {
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
