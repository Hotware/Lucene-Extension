package com.github.hotware.lucene.extension.hsearch.jpa.test.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.search.annotations.ContainedIn;

@Entity
public class AdditionalPlace2 {
	
	private Integer id;
	private AdditionalPlace additionalPlace;
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	
	@OneToOne
	@ContainedIn
	public AdditionalPlace getAdditionalPlace() {
		return additionalPlace;
	}

	public void setAdditionalPlace(AdditionalPlace additionalPlace) {
		this.additionalPlace = additionalPlace;
	}

	
}