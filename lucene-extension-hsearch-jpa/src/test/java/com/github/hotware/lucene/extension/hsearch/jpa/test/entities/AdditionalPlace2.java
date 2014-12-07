package com.github.hotware.lucene.extension.hsearch.jpa.test.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.search.annotations.ContainedIn;

import com.github.hotware.lucene.extension.hsearch.jpa.event.HSearchJPAEventListener;

@Entity
@EntityListeners(HSearchJPAEventListener.class)
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
	
	@OneToOne(mappedBy="additionalPlace2")
	@ContainedIn
	public AdditionalPlace getAdditionalPlace() {
		return additionalPlace;
	}

	public void setAdditionalPlace(AdditionalPlace additionalPlace) {
		this.additionalPlace = additionalPlace;
	}

	
}