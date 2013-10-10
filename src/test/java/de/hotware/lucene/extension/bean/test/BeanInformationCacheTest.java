package de.hotware.lucene.extension.bean.test;

import java.util.List;

import de.hotware.lucene.extension.bean.BeanField;
import de.hotware.lucene.extension.bean.BeanInformationCache;
import de.hotware.lucene.extension.bean.BeanInformationCacheImpl;
import de.hotware.lucene.extension.bean.BeanInformationCache.FieldInformation;
import de.hotware.lucene.extension.bean.analyzer.StockAnalyzerProvider;
import de.hotware.lucene.extension.bean.type.StockType;
import junit.framework.TestCase;

public class BeanInformationCacheTest extends TestCase {

	public final class AnalyzerTestBean {

		//not being serialized -> exception is expected when used with the BeanConverter
		@BeanField(store = true, index = true, type = StockType.StringType.class, analyzerProvider = StockAnalyzerProvider.GermanAnalyzerProvider.class)
		public String test;

	}
	
	public void testBeanInformationCacheImpl() {
		new BeanInformationCacheImpl();
	}
	
	public void testGetFieldInformations() {
		BeanInformationCache cache = new BeanInformationCacheImpl();
		List<FieldInformation> fieldInfos = cache.getFieldInformations(AnalyzerTestBean.class);		
	}
	
	public void testGetPerFieldAnalyzerWrapper() {
		BeanInformationCache cache = new BeanInformationCacheImpl();
		PerFieldAnalyzerWrapper wrapper = cache.getPerFieldAnalyzerWrapper(AnalyzerTestBean.class);		
	}

}
