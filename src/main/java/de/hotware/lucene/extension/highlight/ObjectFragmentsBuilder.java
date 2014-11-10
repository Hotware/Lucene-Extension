/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hotware.lucene.extension.highlight;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.vectorhighlight.FieldFragList;

public interface ObjectFragmentsBuilder {

	/**
	 * create multiple fragments.
	 * 
	 * @param reader
	 *            IndexReader of the index
	 * @param docId
	 *            document id to be highlighter
	 * @param fieldName
	 *            field of the document to be highlighted
	 * @param fieldFragList
	 *            FieldFragList object
	 * @param maxNumFragments
	 *            maximum number of fragments
	 * @param preTags
	 *            pre-tags to be used to highlight terms
	 * @param postTags
	 *            post-tags to be used to highlight terms
	 * @param encoder
	 *            an encoder that generates encoded text
	 * @return created fragments or null when no fragments created. size of the
	 *         array can be less than maxNumFragments
	 * @throws IOException
	 *             If there is a low-level I/O error
	 */
	public <T> List<T> createFragments(IndexReader reader, int docId,
			String fieldName, FieldFragList fieldFragList, int maxNumFragments,
			String[] preTags, String[] postTags, ObjectEncoder<T> encoder)
			throws IOException;

}
