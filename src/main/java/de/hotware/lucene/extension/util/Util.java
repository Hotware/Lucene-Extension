/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.util;

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
