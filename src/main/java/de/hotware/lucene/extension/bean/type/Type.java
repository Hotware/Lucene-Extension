/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.type;

import org.apache.lucene.document.FieldType;

/**
 * the main extension point for providing different types to handle in Lucene
 * Beans <br />
 * <br />
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
	 * but it is discouraged to do so</b>
	 */
	public void configureFieldType(FieldType fieldType);

}
