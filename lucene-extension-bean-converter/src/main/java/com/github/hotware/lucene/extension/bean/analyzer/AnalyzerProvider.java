/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;

import com.github.hotware.lucene.extension.bean.field.FieldInformation;

/**
 * Extension point for providing custom Analyzers
 * 
 * @author Martin Braun
 */
public interface AnalyzerProvider {

	/**
	 * @param fieldInformation
	 *            info about the field, or null if default behaviour
	 */
	public Analyzer getAnalyzer(FieldInformation fieldInformation);

}
