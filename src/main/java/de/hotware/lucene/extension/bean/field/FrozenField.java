package de.hotware.lucene.extension.bean.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

/**
 * We don't want people to tamper with the field's setter methods. so we have
 * this 'frozen' class for this.
 * <br />
 * <br />
 * This will not prevent all tampering. As users will still have access to the
 * declaring class but will make people think about what they are doing first
 * 
 * @author Martin Braun
 */
public final class FrozenField implements Member {

	private final Field field;

	public FrozenField(Field field) {
		this.field = field;
	}

	public Class<?> getDeclaringClass() {
		throw new UnsupportedOperationException("nope. you shouldn't need this!");
	}

	public String getName() {
		return field.getName();
	}

	public int getModifiers() {
		return field.getModifiers();
	}

	public boolean isAccessible() {
		return field.isAccessible();
	}

	public boolean isEnumConstant() {
		return field.isEnumConstant();
	}

	public boolean isSynthetic() {
		return field.isSynthetic();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Type getGenericType() {
		return field.getGenericType();
	}

	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationClass) {
		return field.isAnnotationPresent(annotationClass);
	}

	public Annotation[] getAnnotations() {
		return field.getAnnotations();
	}

	public String toGenericString() {
		return field.toGenericString();
	}

	public Object get(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.get(obj);
	}

	public boolean getBoolean(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getBoolean(obj);
	}

	public byte getByte(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getByte(obj);
	}

	public char getChar(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getChar(obj);
	}

	public short getShort(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getShort(obj);
	}

	public int getInt(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getInt(obj);
	}

	public long getLong(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getLong(obj);
	}

	public float getFloat(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getFloat(obj);
	}

	public double getDouble(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		return field.getDouble(obj);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return field.getAnnotation(annotationClass);
	}

	public Annotation[] getDeclaredAnnotations() {
		return field.getDeclaredAnnotations();
	}

	public void set(Object obj, Object value) throws IllegalArgumentException,
			IllegalAccessException {
		field.set(obj, value);
	}

	public void setBoolean(Object obj, boolean z)
			throws IllegalArgumentException, IllegalAccessException {
		field.setBoolean(obj, z);
	}

	public void setByte(Object obj, byte b) throws IllegalArgumentException,
			IllegalAccessException {
		field.setByte(obj, b);
	}

	public void setChar(Object obj, char c) throws IllegalArgumentException,
			IllegalAccessException {
		field.setChar(obj, c);
	}

	public void setShort(Object obj, short s) throws IllegalArgumentException,
			IllegalAccessException {
		field.setShort(obj, s);
	}

	public void setInt(Object obj, int i) throws IllegalArgumentException,
			IllegalAccessException {
		field.setInt(obj, i);
	}

	public void setLong(Object obj, long l) throws IllegalArgumentException,
			IllegalAccessException {
		field.setLong(obj, l);
	}

	public void setFloat(Object obj, float f) throws IllegalArgumentException,
			IllegalAccessException {
		field.setFloat(obj, f);
	}

	public void setDouble(Object obj, double d)
			throws IllegalArgumentException, IllegalAccessException {
		field.setDouble(obj, d);
	}

}
