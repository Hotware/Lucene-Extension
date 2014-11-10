package de.hotware.lucene.extension.highlight;

import org.apache.lucene.document.Field;
import org.apache.lucene.search.vectorhighlight.FieldFragList.WeightedFragInfo;

/**
 * @author Martin Braun
 * @param <T>
 */
public interface ObjectEncoder<T> {
	
	public T encode(WeightedFragInfo fragInfo, Field[] values);

}
