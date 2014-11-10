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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FieldTermStack;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;

/**
 * Utility class to be used with the utilities from
 * {@link org.apache.lucene.search.vectorhighlight.FastVectorHighlighter} that
 * exposes more of the api.
 * 
 * <br />
 * 
 * Most of the code is copy pasted and we only support single valued fields for
 * simplicity.
 * 
 * @author Martin Braun
 * 
 */
public class FVHighlighterUtil {

	private final int phraseLimit;

	public FVHighlighterUtil(int phraseLimit) {
		this.phraseLimit = phraseLimit;
	}

	/**
	 * Return the best fragments. Matches are scanned from matchedFields and
	 * turned into fragments against storedField. The highlighting may not make
	 * sense if matchedFields has matches with offsets that don't correspond
	 * features in storedField. It will outright throw a
	 * {@code StringIndexOutOfBoundsException} if matchedFields produces offsets
	 * outside of storedField. As such it is advisable that all matchedFields
	 * share the same source as storedField or are at least a prefix of it.
	 * 
	 * @param fieldQuery
	 *            {@link FieldQuery} object
	 * @param reader
	 *            {@link IndexReader} of the index
	 * @param docId
	 *            document id to be highlighted
	 * @param storedField
	 *            field of the document that stores the text
	 * @param matchedFields
	 *            fields of the document to scan for matches
	 * @param fragCharSize
	 *            the length (number of chars) of a fragment
	 * @param maxNumFragments
	 *            maximum number of fragments
	 * @param fragListBuilder
	 *            {@link FragListBuilder} object
	 * @param fragmentsBuilder
	 *            {@link FragmentsBuilder} object
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
	public final <T> List<T> getBestFragments(final FieldQuery fieldQuery,
			IndexReader reader, int docId, String storedField,
			Set<String> matchedFields, int fragCharSize, int maxNumFragments,
			FragListBuilder fragListBuilder,
			ObjectFragmentsBuilder fragmentsBuilder, String[] preTags,
			String[] postTags, ObjectEncoder<T> encoder) throws IOException {
		FieldFragList fieldFragList = getFieldFragList(fragListBuilder,
				fieldQuery, reader, docId, matchedFields, fragCharSize);
		return fragmentsBuilder.createFragments(reader, docId, storedField,
				fieldFragList, maxNumFragments, preTags, postTags, encoder);
	}

	/**
	 * Build a FieldFragList for more than one field.
	 */
	public FieldFragList getFieldFragList(FragListBuilder fragListBuilder,
			final FieldQuery fieldQuery, IndexReader reader, int docId,
			Set<String> matchedFields, int fragCharSize) throws IOException {
		Iterator<String> matchedFieldsItr = matchedFields.iterator();
		if (!matchedFieldsItr.hasNext()) {
			throw new IllegalArgumentException(
					"matchedFields must contain at least one field name.");
		}
		FieldPhraseList[] toMerge = new FieldPhraseList[matchedFields.size()];
		int i = 0;
		while (matchedFieldsItr.hasNext()) {
			FieldTermStack stack = new FieldTermStack(reader, docId,
					matchedFieldsItr.next(), fieldQuery);
			toMerge[i++] = new FieldPhraseList(stack, fieldQuery, phraseLimit);
		}
		return fragListBuilder.createFieldFragList(
				new FieldPhraseList(toMerge), fragCharSize);
	}

}
