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
package com.github.hotware.lucene.extension.highlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldFragList.WeightedFragInfo;
import org.apache.lucene.search.vectorhighlight.FieldFragList.WeightedFragInfo.SubInfo;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList.WeightedPhraseInfo.Toffs;

/**
 * @author Martin Braun
 */
public abstract class BaseObjectFragmentsBuilder implements
		ObjectFragmentsBuilder {

	protected final boolean discreteMultiValueHighlighting;

	public BaseObjectFragmentsBuilder(boolean discreteMultiValueHighlighting) {
		this.discreteMultiValueHighlighting = discreteMultiValueHighlighting;
	}

	public abstract List<WeightedFragInfo> getWeightedFragInfoList(
			List<WeightedFragInfo> src);

	@Override
	public <T> List<T> createFragments(IndexReader reader, int docId,
			String fieldName, FieldFragList fieldFragList, int maxNumFragments,
			ObjectEncoder<T> encoder) throws IOException {

		if (maxNumFragments < 0) {
			throw new IllegalArgumentException("maxNumFragments("
					+ maxNumFragments + ") must be positive number.");
		}

		List<WeightedFragInfo> fragInfos = fieldFragList.getFragInfos();
		Field[] values = getFields(reader, docId, fieldName);
		if (values.length == 0) {
			return null;
		}

		if (discreteMultiValueHighlighting && values.length > 1) {
			fragInfos = discreteMultiValueHighlighting(fragInfos, values);
		}

		fragInfos = getWeightedFragInfoList(fragInfos);
		int limitFragments = maxNumFragments < fragInfos.size() ? maxNumFragments
				: fragInfos.size();
		List<T> fragments = new ArrayList<>(limitFragments);

		for (int i = 0; i < limitFragments; ++i) {
			fragments.add(encoder.encode(fragInfos.get(i), values));
		}
		return fragments;
	}

	protected List<WeightedFragInfo> discreteMultiValueHighlighting(
			List<WeightedFragInfo> fragInfos, Field[] fields) {
		Map<String, List<WeightedFragInfo>> fieldNameToFragInfos = new HashMap<>();
		for (Field field : fields) {
			fieldNameToFragInfos.put(field.name(),
					new ArrayList<WeightedFragInfo>());
		}

		fragInfos: for (WeightedFragInfo fragInfo : fragInfos) {
			int fieldStart;
			int fieldEnd = 0;
			for (Field field : fields) {
				if (field.stringValue().isEmpty()) {
					fieldEnd++;
					continue;
				}
				fieldStart = fieldEnd;
				fieldEnd += field.stringValue().length() + 1; // + 1 for going
																// to next field
																// with same
																// name.

				if (fragInfo.getStartOffset() >= fieldStart
						&& fragInfo.getEndOffset() >= fieldStart
						&& fragInfo.getStartOffset() <= fieldEnd
						&& fragInfo.getEndOffset() <= fieldEnd) {
					fieldNameToFragInfos.get(field.name()).add(fragInfo);
					continue fragInfos;
				}

				if (fragInfo.getSubInfos().isEmpty()) {
					continue fragInfos;
				}

				Toffs firstToffs = fragInfo.getSubInfos().get(0)
						.getTermsOffsets().get(0);
				if (fragInfo.getStartOffset() >= fieldEnd
						|| firstToffs.getStartOffset() >= fieldEnd) {
					continue;
				}

				int fragStart = fieldStart;
				if (fragInfo.getStartOffset() > fieldStart
						&& fragInfo.getStartOffset() < fieldEnd) {
					fragStart = fragInfo.getStartOffset();
				}

				int fragEnd = fieldEnd;
				if (fragInfo.getEndOffset() > fieldStart
						&& fragInfo.getEndOffset() < fieldEnd) {
					fragEnd = fragInfo.getEndOffset();
				}

				List<SubInfo> subInfos = new ArrayList<>();
				Iterator<SubInfo> subInfoIterator = fragInfo.getSubInfos()
						.iterator();
				float boost = 0.0f; // The boost of the new info will be the sum
									// of the boosts of its SubInfos
				while (subInfoIterator.hasNext()) {
					SubInfo subInfo = subInfoIterator.next();
					List<Toffs> toffsList = new ArrayList<>();
					Iterator<Toffs> toffsIterator = subInfo.getTermsOffsets()
							.iterator();
					while (toffsIterator.hasNext()) {
						Toffs toffs = toffsIterator.next();
						if (toffs.getStartOffset() >= fieldStart
								&& toffs.getEndOffset() <= fieldEnd) {

							toffsList.add(toffs);
							toffsIterator.remove();
						}
					}
					if (!toffsList.isEmpty()) {
						subInfos.add(new SubInfo(subInfo.getText(), toffsList,
								subInfo.getSeqnum(), subInfo.getBoost()));
						boost += subInfo.getBoost();
					}

					if (subInfo.getTermsOffsets().isEmpty()) {
						subInfoIterator.remove();
					}
				}
				WeightedFragInfo weightedFragInfo = new WeightedFragInfo(
						fragStart, fragEnd, subInfos, boost);
				fieldNameToFragInfos.get(field.name()).add(weightedFragInfo);
			}
		}

		List<WeightedFragInfo> result = new ArrayList<>();
		for (List<WeightedFragInfo> weightedFragInfos : fieldNameToFragInfos
				.values()) {
			result.addAll(weightedFragInfos);
		}
		Collections.sort(result, new Comparator<WeightedFragInfo>() {

			@Override
			public int compare(FieldFragList.WeightedFragInfo info1,
					FieldFragList.WeightedFragInfo info2) {
				return info1.getStartOffset() - info2.getStartOffset();
			}

		});

		return result;
	}

	private static Field[] getFields(IndexReader reader, int docId,
			final String fieldName) throws IOException {
		// according to javadoc, doc.getFields(fieldName) cannot be used with
		// lazy loaded field???
		final List<Field> fields = new ArrayList<>();
		reader.document(docId, new StoredFieldVisitor() {

			@Override
			public void stringField(FieldInfo fieldInfo, String value) {
				FieldType ft = new FieldType(TextField.TYPE_STORED);
				ft.setStoreTermVectors(fieldInfo.hasVectors());
				fields.add(new Field(fieldInfo.name, value, ft));
			}

			@Override
			public Status needsField(FieldInfo fieldInfo) {
				return fieldInfo.name.equals(fieldName) ? Status.YES
						: Status.NO;
			}

		});
		return fields.toArray(new Field[fields.size()]);
	}

}
