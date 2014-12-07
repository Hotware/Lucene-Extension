package com.github.hotware.lucene.extension.hsearch.jpa.test.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostUpdate;
import javax.persistence.Transient;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.github.hotware.lucene.extension.hsearch.jpa.event.HSearchJPAEventListener;

@Entity
@Indexed
@EntityListeners({HSearchJPAEventListener.class})
public class Place {

	@PostUpdate
	public void postUpdate() {
		System.out.println("updated Place");
	}

	private Integer id;
	private String name;
	private Set<Sorcerer> sorcerers = new HashSet<>();
	private AdditionalPlace additionalPlace;
	private List<EmbeddableInfo> info;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Field(store = Store.NO, index = Index.YES)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@IndexedEmbedded(depth = 3)
	@ContainedIn
	@OneToMany(cascade = CascadeType.ALL)
	public Set<Sorcerer> getSorcerers() {
		return sorcerers;
	}

	public void setSorcerers(Set<Sorcerer> sorcerers) {
		this.sorcerers = sorcerers;
	}

	@Override
	public String toString() {
		return "Place [id=" + id + ", name=" + name + ", sorcerers="
				+ sorcerers + "]";
	}

	@OneToOne
	public AdditionalPlace getAdditionalPlace() {
		return additionalPlace;
	}

	public void setAdditionalPlace(AdditionalPlace additionalPlace) {
		this.additionalPlace = additionalPlace;
	}

	// TODO: test with this
	// @ElementCollection
	// @CollectionTable(name = "PHONE", joinColumns = @JoinColumn(name =
	// "OWNER_ID"))
	@Transient
	public List<EmbeddableInfo> getInfo() {
		return info;
	}

	public void setInfo(List<EmbeddableInfo> info) {
		this.info = info;
	}

}