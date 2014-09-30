package de.hotware.lucene.extension.manager;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import de.hotware.lucene.extension.bean.BeanConverter;
import de.hotware.lucene.extension.bean.BeanConverterImpl;
import de.hotware.lucene.extension.bean.BeanInformationCache;
import de.hotware.lucene.extension.bean.BeanInformationCacheImpl;

public abstract class BaseLuceneManager implements LuceneManager {

	private final Lock lock;
	private final BeanConverter beanConverter;
	private final Directory directory;
	private final IndexWriterConfig indexWriterConfig;
	private final BeanInformationCache beanInformationCache;
	private IndexWriter indexWriter;
	private IndexSearcher indexSearcher;
	private DirectoryReader indexReader;

	public BaseLuceneManager(Directory directory) {
		this.lock = new ReentrantLock();
		this.beanConverter = new BeanConverterImpl(
				this.beanInformationCache = new BeanInformationCacheImpl());
		try {
			this.directory = directory;
			this.indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_9,
					new StandardAnalyzer(Version.LUCENE_4_9));
			this.indexWriter = new IndexWriter(this.directory,
					this.indexWriterConfig);
			this.indexReader = DirectoryReader.open(this.directory);
			this.indexSearcher = new IndexSearcher(this.indexReader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public BeanConverter getBeanConverter() {
		return this.beanConverter;
	}

	@Override
	public IndexWriter getIndexWriter() {
		this.lock.lock();
		try {
			return this.indexWriter;
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		try {
			this.lock.lock();
			try {
				DirectoryReader newReader = DirectoryReader
						.openIfChanged(this.indexReader);
				if (newReader != null) {
					this.indexReader = newReader;
					this.indexSearcher = new IndexSearcher(this.indexReader);
				}
			} finally {
				this.lock.unlock();
			}
			return this.indexSearcher;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void shouldReopenIndexWriter() {
		this.lock.lock();
		try {
			try {
				this.indexWriter.close();
				this.indexWriter = new IndexWriter(this.directory,
						this.indexWriterConfig);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public BeanInformationCache getBeanInformationCache() {
		return this.beanInformationCache;
	}

}
