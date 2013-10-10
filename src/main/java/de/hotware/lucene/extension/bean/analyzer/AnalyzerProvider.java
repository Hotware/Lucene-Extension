package de.hotware.lucene.extension.bean.analyzer;

import org.apache.lucene.analysis.Analyzer;


public interface AnalyzerProvider {
	
	public Analyzer getAnalyzer();

}
