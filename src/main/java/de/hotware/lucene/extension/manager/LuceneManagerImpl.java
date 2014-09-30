package de.hotware.lucene.extension.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class LuceneManagerImpl implements LuceneManager {

	private final Lock lock;
	private final BeanConverter beanConverter;
	private final Directory directory;
	private final IndexWriterConfig indexWriterConfig;
	private final BeanInformationCache beanInformationCache;
	private IndexWriter indexWriter;
	private IndexSearcher indexSearcher;
	private DirectoryReader indexReader;
	private boolean closed;

	public LuceneManagerImpl(Directory directory) throws IOException {
		this.lock = new ReentrantLock();
		this.beanConverter = new BeanConverterImpl(
				this.beanInformationCache = new BeanInformationCacheImpl());
		this.directory = directory;
		this.indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_9,
				new StandardAnalyzer(Version.LUCENE_4_9));
		try {
			this.indexWriter = new IndexWriter(this.directory,
					this.indexWriterConfig);
			this.indexReader = DirectoryReader.open(this.directory);
			this.indexSearcher = new IndexSearcher(this.indexReader);
		} catch(IOException e) {
			this.close();
			throw e;
		}
	}

	@Override
	public final BeanConverter getBeanConverter() {
		return this.beanConverter;
	}

	@Override
	public final IndexWriter getIndexWriter() {
		this.lock.lock();
		try {
			this.checkOpen();
			return this.indexWriter;
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public final IndexSearcher getIndexSearcher() throws IOException {
		this.lock.lock();
		try {
			this.checkOpen();
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
	}

	@Override
	public final void shouldReopenIndexWriter() throws IOException {
		this.lock.lock();
		try {
			this.checkOpen();
			this.indexWriter.close();
			this.indexWriter = new IndexWriter(this.directory,
					this.indexWriterConfig);
		} finally {
			this.lock.unlock();
		}
	}

	public final void close() throws IOException {
		this.lock.lock();
		try {
			this.closed = true;
			List<Exception> exceptions = new ArrayList<>();
			try {
				if(this.indexWriter != null) {
					this.indexWriter.close();
				}
			} catch (IOException e) {
				exceptions.add(e);
			}
			try {
				if(this.indexReader != null) {
					this.indexReader.close();
				}
			} catch (IOException e) {
				exceptions.add(e);
			}
			if (exceptions.size() > 0) {
				throw new IOException("IOException(s) while closing"
						+ exceptions);
			}
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public final BeanInformationCache getBeanInformationCache() {
		return this.beanInformationCache;
	}

	private void checkOpen() {
		if (this.closed) {
			throw new IllegalStateException("has been closed");
		}
	}

}
