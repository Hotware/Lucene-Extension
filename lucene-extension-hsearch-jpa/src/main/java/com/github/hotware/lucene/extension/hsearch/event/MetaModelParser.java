package com.github.hotware.lucene.extension.hsearch.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;

import javax.persistence.metamodel.SingularAttribute;

import com.github.hotware.lucene.extension.hsearch.entity.annotation.Parent;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

public class MetaModelParser {

	private final Map<Class<?>, Map<Class<?>, Function<Object, Object>>> rootParentAccessors = new HashMap<>();
	private final Map<Class<?>, ManagedType<?>> managedTypes = new HashMap<>();
	private final Map<Class<?>, Boolean> isRootType = new HashMap<>();

	public Map<Class<?>, Map<Class<?>, Function<Object, Object>>> getRootParentAccessors() {
		return rootParentAccessors;
	}

	public Map<Class<?>, ManagedType<?>> getManagedTypes() {
		return managedTypes;
	}

	public Map<Class<?>, Boolean> getIsRootType() {
		return isRootType;
	}

	public void parse(Metamodel metaModel) {
		this.rootParentAccessors.clear();
		this.managedTypes.clear();
		this.managedTypes.putAll(metaModel.getManagedTypes().stream()
				.filter((meta3) -> {
					return meta3 instanceof EntityType;
				}).collect(Collectors.toMap((meta) -> {
					return meta.getJavaType();
				}, (meta2) -> {
					return meta2;
				})));
		Set<EntityType<?>> emptyVisited = Collections.emptySet();
		for (EntityType<?> curEntType : metaModel.getEntities()) {
			// we only consider Entities that are @Indexed here
			if (curEntType.getJavaType().isAnnotationPresent(Indexed.class)) {
				this.isRootType.put(curEntType.getJavaType(), true);
				Map<Class<? extends Annotation>, List<Attribute<?, ?>>> attributeForAnnotationType = this
						.buildAttributeForAnnotationType(curEntType);
				// and do the recursion
				this.doRecursion(attributeForAnnotationType, curEntType,
						emptyVisited);
			}
		}
	}

	public void parse(EntityType<?> curEntType, Class<?> cameFrom,
			Set<EntityType<?>> visited) {
		Map<Class<? extends Annotation>, List<Attribute<?, ?>>> attributeForAnnotationType = this
				.buildAttributeForAnnotationType(curEntType);

		Function<Object, Object> toRoot;
		// first of all, lets build the parentAccessor for this entity
		{
			Stream<Attribute<?, ?>> cameFromAttributes = attributeForAnnotationType
					.get(Parent.class).stream().filter((attribute) -> {
						return attribute.getJavaType().equals(cameFrom);
					});
			if (cameFromAttributes.count() != 1) {
				throw new IllegalArgumentException(
						"entity: "
								+ curEntType.getJavaType()
								+ " has not exactly 1 @Parent for each Index-parent specified");
			}
			Attribute<?, ?> toParentAttribute = cameFromAttributes.findFirst()
					.get();
			toRoot = (object) -> {
				Object parentOfThis = member(toParentAttribute.getJavaMember(),
						object);
				return parentOfThis;
			};
			this.getParentFunctionList(curEntType.getJavaType()).put(
					curEntType.getJavaType(), toRoot);
		}

		// and do the recursion
		this.doRecursion(attributeForAnnotationType, curEntType, visited);
	}

	private Map<Class<? extends Annotation>, List<Attribute<?, ?>>> buildAttributeForAnnotationType(
			EntityType<?> entType) {
		Map<Class<? extends Annotation>, List<Attribute<?, ?>>> attributeForAnnotationType = new HashMap<>();
		entType.getAttributes().forEach((declared) -> {
			Member member = declared.getJavaMember();
			Class<? extends Annotation> annotationClass;
			if (isAnnotationPresent(member, IndexedEmbedded.class)) {
				annotationClass = IndexedEmbedded.class;
			} else if (isAnnotationPresent(member, Parent.class)) {
				annotationClass = Parent.class;
			} else {
				// this member is not of interest for us
				return;
			}
			List<Attribute<?, ?>> list = attributeForAnnotationType
					.computeIfAbsent(annotationClass, (key) -> {
						return new ArrayList<>();
					});
			list.add(declared);
		});
		return attributeForAnnotationType;
	}

	private void doRecursion(
			Map<Class<? extends Annotation>, List<Attribute<?, ?>>> attributeForAnnotationType,
			EntityType<?> entType, Set<EntityType<?>> visited) {
		// we don't change the original visited set.
		Set<EntityType<?>> newVisited = new HashSet<>(visited);
		// add the current entityType to the set
		newVisited.add(entType);
		// we don't want to visit already visited entities
		// this should be okay to do, as cycles don't matter
		// as long as we start from the original
		attributeForAnnotationType
				.get(IndexedEmbedded.class)
				.stream()
				.filter((attribute) -> {
					boolean notVisited = !visited.contains(attribute);
					PersistentAttributeType attrType = attribute
							.getPersistentAttributeType();
					boolean otherEndIsEntity = attrType != PersistentAttributeType.BASIC
							&& attrType != PersistentAttributeType.EMBEDDED;
					if (attrType == PersistentAttributeType.ELEMENT_COLLECTION) {
						throw new IllegalArgumentException(
								"Element Collections are not allowe as with plain JPA "
										+ "we cannot reliably get the events to update our index!");
					}
					if (attrType == PersistentAttributeType.MANY_TO_MANY) {
						throw new IllegalArgumentException(
								"MANY_TO_MANY is not allowed as with plain JPA "
										+ "we cannot reliably get the events to update our index!"
										+ " Please map the Bridge table itself. "
										+ "Btw.: Map all your Bridge tables when using this class!");
					}
					return notVisited && otherEndIsEntity;
				})
				.forEach(
						(attribute) -> {
							Class<?> entityTypeClass;
							if (attribute instanceof PluralAttribute<?, ?, ?>) {
								entityTypeClass = (((PluralAttribute<?, ?, ?>) attribute)
										.getElementType().getJavaType());
							} else if (attribute instanceof SingularAttribute<?, ?>) {
								entityTypeClass = (((SingularAttribute<?, ?>) attribute)
										.getType().getJavaType());
							} else {
								throw new AssertionError(
										"attributes have to either be "
												+ "instanceof PluralAttribute or SingularAttribute "
												+ "at this point");
							}
							this.parse((EntityType<?>) this.managedTypes
									.get(entityTypeClass), entType
									.getJavaType(), newVisited);
						});
	}

	private Map<Class<?>, Function<Object, Object>> getParentFunctionList(
			Class<?> clazz) {
		return this.rootParentAccessors.computeIfAbsent(clazz, (key) -> {
			return new HashMap<>();
		});
	}

	public static boolean isAnnotationPresent(Member member,
			Class<? extends Annotation> annotationClass) {
		boolean ret = false;
		if (member instanceof Method) {
			Method method = (Method) member;
			ret = method.isAnnotationPresent(annotationClass);
		} else if (member instanceof Field) {
			Field field = (Field) member;
			ret = field.isAnnotationPresent(annotationClass);
		} else {
			throw new AssertionError("member should either be Field or Member");
		}
		return ret;
	}

	public static Object member(Member member, Object object) {
		try {
			Object ret;
			if (member instanceof Method) {
				Method method = (Method) member;
				ret = method.invoke(object);
			} else if (member instanceof Field) {
				Field field = (Field) member;
				ret = field.get(object);
			} else {
				throw new AssertionError(
						"member should either be Field or Member");
			}
			return ret;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
