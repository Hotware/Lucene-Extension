/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.field;

import java.util.List;

/**
 * @author Martin Braun
 */
public interface BeanInformationCache {

	public List<FieldInformation> getFieldInformations(Class<?> clazz);

}
