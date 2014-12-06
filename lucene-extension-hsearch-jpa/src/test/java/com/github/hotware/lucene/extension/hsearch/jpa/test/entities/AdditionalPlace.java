package com.github.hotware.lucene.extension.hsearch.jpa.test.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.search.annotations.ContainedIn;

@Entity
public class AdditionalPlace {

	private Integer id;
	private Place place;
	private AdditionalPlace2 additionalPlace2;

	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	@OneToOne
	public AdditionalPlace2 getAdditionalPlace2() {
		return additionalPlace2;
	}

	public void setAdditionalPlace2(AdditionalPlace2 additionalPlace2) {
		this.additionalPlace2 = additionalPlace2;
	}

	@ContainedIn
	@OneToOne(mappedBy = "additionalPlace")
	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

}
