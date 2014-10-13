package de.hotware.lucene.extension.bean;

import java.lang.annotation.Annotation;

/**
 * Utility methods
 * 
 * @author Martin Braun
 */
public class Util {

	public static Object getDefaultValueForAnnotationMethod(
			Class<? extends Annotation> annotationClass, String name) {
		try {
			return annotationClass.getDeclaredMethod(name).getDefaultValue();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
