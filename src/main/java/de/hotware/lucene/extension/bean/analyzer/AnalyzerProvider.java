package de.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;

/**
 * Extension point for providing custom Analyzers
 * 
 * @author Martin Braun
 */
public interface AnalyzerProvider {
	
	public Analyzer getAnalyzer();

}
