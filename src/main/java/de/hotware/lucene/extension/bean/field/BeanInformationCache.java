package de.hotware.lucene.extension.bean.field;

import java.util.List;

/**
 * @author Martin Braun
 */
public interface BeanInformationCache {

	public List<FieldInformation> getFieldInformations(Class<?> clazz);

}
