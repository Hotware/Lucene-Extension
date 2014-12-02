/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.type;

import com.github.hotware.lucene.extension.bean.annotations.BeanField;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

/**
 * the main extension point for providing different types to handle in Lucene
 * Beans <br>
 * <br>
 * For slightly easier usage use {@link BaseSingularType}
 * 
 * @author Martin Braun
 */
public interface Type {

	/**
	 * set the options for the FieldType you want the Field in your Document to
	 * have here, as it gets frozen afterwards. this is called after all other
	 * stuff is set up in the fieldtype.
	 * 
	 * <b>Note: you can change the index, store and tokenized attribute in here,
	 * but it is discouraged to do so. However this should be used to change the
	 * {@link FieldInfo.DocValuesType} with
	 * {@link FieldType#setDocValueType(org.apache.lucene.index.FieldInfo.DocValuesType)}
	 * as the default is null and this cannot be handled in {@link BeanField}
	 * </b>
	 */
	public void configureFieldType(FieldType fieldType);

}
