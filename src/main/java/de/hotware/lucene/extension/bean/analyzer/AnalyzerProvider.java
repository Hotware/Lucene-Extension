package de.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;

import de.hotware.lucene.extension.bean.field.FieldInformation;

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
