package com.github.hotware.lucene.extension.hsearch.event;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

public class MetaModelParser {

	private Set<EntityType<?>> alreadyParsed = new HashSet<>();
	private Map<Class<?>, List<Function<Object, Object>>> parentAccessors = new HashMap<>();

	public void parse(Metamodel metaModel) {
		Map<Class<?>, ManagedType<?>> managedTypes = metaModel
				.getManagedTypes().stream()
				.collect(Collectors.toMap((meta) -> {
					return meta.getJavaType();
				}, (meta2) -> {
					return meta2;
				}));
		for (EntityType<?> entType : metaModel.getEntities()) {
			parentAccessors.computeIfAbsent(entType.getJavaType(), (clazz) -> {
				return new ArrayList<>();
			}).add((obj) -> {
				//we return null as we are already the parent
				return null;
			});
			for (Attribute<?, ?> declared : entType.getDeclaredAttributes()) {
				PersistentAttributeType type = declared
						.getPersistentAttributeType();
				if (type != PersistentAttributeType.BASIC) {
					Member member = declared.getJavaMember();
					Class<?> propertyType;
					if (member instanceof Method) {
						Method method = (Method) member;
						propertyType = method.getReturnType();
					} else if (member instanceof Field) {
						Field field = (Field) member;
						propertyType = field.getType();
					} else {
						throw new AssertionError(
								"member should either be Field or Member");
					}
					ManagedType<?> managedType = managedTypes.get(propertyType);
					//recursion here
				}
			}
		}
	}
}
