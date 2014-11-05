package de.hotware.lucene.extension.bean;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import org.apache.lucene.index.FieldInfo.IndexOptions;

import de.hotware.lucene.extension.bean.analyzer.AnalyzerProvider;
import de.hotware.lucene.extension.bean.analyzer.StockAnalyzerProvider;
import de.hotware.lucene.extension.bean.type.StockType;
import de.hotware.lucene.extension.bean.type.Type;

/**
 * Annotation for Beans that should be stored into lucene Use this class in
 * addition with a BeanConverter Implementation
 * 
 * From the Lucene-Wiki: <br />
 * What is the different between Stored, Tokenized, Indexed, and Vector?
 * 
 * Stored = as-is value stored in the Lucene index Tokenized = field is analyzed
 * using the specified Analyzer - the tokens emitted are indexed Indexed = the
 * text (either as-is with keyword fields, or the tokens from tokenized fields)
 * is made searchable (aka inverted) Vectored = term frequency per document is
 * stored in the index in an easily retrievable fashion.
 * 
 * <br />
 * <br />
 * Note: Default values correspond to the way
 * {@link org.apache.lucene.document.FieldType} handles things. The
 * {@link org.apache.lucene.index.FieldInfo.DocValuesType} has to be handled via
 * a custom Type as it's default is null in the FieldType class.
 * 
 * @author Martin Braun
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface BeanField {

	public String name() default "#DEFAULT";

	public boolean index() default false;

	public boolean store() default false;

	public boolean tokenized() default true;

	public boolean storeTermVectors() default false;

	public boolean storeTermVectorPositions() default false;

	public boolean storeTermVectorOffsets() default false;

	public boolean storeTermVectorPayloads() default false;

	public boolean omitNorms() default false;

	public IndexOptions indexOptions() default IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;

	public Class<? extends Type> type() default StockType.SerializeType.class;

	public Class<? extends AnalyzerProvider> analyzerProvider() default StockAnalyzerProvider.StandardAnalyzerProvider.class;

}
