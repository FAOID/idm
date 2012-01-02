package org.openforis.idm.model;

import org.openforis.idm.metamodel.NumberAttributeDefinition;

/**
 * @author G. Miceli
 * @author M. Togna
 */
public class IntegerAttribute extends NumberAttribute<Integer> {

	public IntegerAttribute(NumberAttributeDefinition definition) {
		super(definition);
		if ( !definition.isInteger() ) {
			throw new IllegalArgumentException("Attempted to create IntegerAttribute with real NumberDefinition");
		}
	}
	
}
