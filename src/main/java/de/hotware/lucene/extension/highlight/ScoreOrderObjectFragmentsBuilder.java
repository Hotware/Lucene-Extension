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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.search.vectorhighlight.FieldFragList.WeightedFragInfo;

/** 
 * @author (adaption: Martin Braun)
 */
public class ScoreOrderObjectFragmentsBuilder extends BaseObjectFragmentsBuilder {

	public ScoreOrderObjectFragmentsBuilder(
			boolean discreteMultiValueHighlighting) {
		super(discreteMultiValueHighlighting);
	}

	/**
	 * Sort by score the list of WeightedFragInfo
	 */
	@Override
	public List<WeightedFragInfo> getWeightedFragInfoList(
			List<WeightedFragInfo> src) {
		Collections.sort(src, new ScoreComparator());
		return src;
	}

	/**
	 * Comparator for {@link WeightedFragInfo} by boost, breaking ties by
	 * offset.
	 */
	public static class ScoreComparator implements Comparator<WeightedFragInfo> {

		@Override
		public int compare(WeightedFragInfo o1, WeightedFragInfo o2) {
			if (o1.getTotalBoost() > o2.getTotalBoost())
				return -1;
			else if (o1.getTotalBoost() < o2.getTotalBoost())
				return 1;
			// if same score then check startOffset
			else {
				if (o1.getStartOffset() < o2.getStartOffset())
					return -1;
				else if (o1.getStartOffset() > o2.getStartOffset())
					return 1;
			}
			return 0;
		}
	}
	
}
