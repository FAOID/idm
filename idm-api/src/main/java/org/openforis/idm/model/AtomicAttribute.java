package org.openforis.idm.model;

import org.openforis.idm.metamodel.AttributeDefinition;

public abstract class AtomicAttribute<D extends AttributeDefinition, V>  extends Attribute<D,V> {

	protected AtomicAttribute(D definition) {
		super(definition, 1);
	}

	@Override
	public V getValue() {
		Field<V> field = getField();
		return field.getValue();
	}

	@Override
	public void setValue(V value) {
		Field<V> field = getField();
		field.setValue(value);
	}

	@SuppressWarnings("unchecked")
	public Field<V> getField() {
		return (Field<V>) getField(0);
	}
}