package de.hotware.lucene.extension.bean.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import de.hotware.lucene.extension.bean.BeanField;
import de.hotware.lucene.extension.bean.BeanInformationCache;
import de.hotware.lucene.extension.bean.BeanInformationCacheImpl;
import de.hotware.lucene.extension.bean.analyzer.StockAnalyzerProvider;
import de.hotware.lucene.extension.bean.type.StockType;
import junit.framework.TestCase;

public class BeanInformationCacheTest extends TestCase {

	public final class AnalyzerTestBean {

		// not being serialized -> exception is expected when used with the
		// BeanConverter
		@BeanField(store = true, index = true, type = StockType.StringType.class, analyzerProvider = StockAnalyzerProvider.GermanAnalyzerProvider.class)
		public String test;

	}
	@SuppressWarnings("unchecked")
	public static <T extends Analyzer> Class<T> getAnalyzer(
			PerFieldAnalyzerWrapper wrapper, String name, Class<T> analyzer)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Method getterMethod = wrapper.getClass().getDeclaredMethod(
				"getWrappedAnalyzer", String.class);
		getterMethod.setAccessible(true);
		return (Class<T>) getterMethod.invoke(wrapper, name).getClass();
	}

	public void testBeanInformationCacheImpl() {
		new BeanInformationCacheImpl();
	}

	public void testGetFieldInformations() {
		BeanInformationCache cache = new BeanInformationCacheImpl();
		cache.getFieldInformations(AnalyzerTestBean.class);
	}

	public void testGetPerFieldAnalyzerWrapper() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		BeanInformationCache cache = new BeanInformationCacheImpl();
		PerFieldAnalyzerWrapper wrapper = cache
				.getPerFieldAnalyzerWrapper(AnalyzerTestBean.class);

		assertEquals(GermanAnalyzer.class, getAnalyzer(wrapper, "test", GermanAnalyzer.class));
	}

}
