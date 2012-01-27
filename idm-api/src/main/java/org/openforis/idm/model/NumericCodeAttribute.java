package org.openforis.idm.model;

import org.openforis.idm.metamodel.CodeAttributeDefinition;

/**
 * @author G. Miceli
 * @author M. Togna
 */
public class NumericCodeAttribute extends CodeAttribute<NumericCode> {

	public NumericCodeAttribute(CodeAttributeDefinition definition) {
		super(definition);
		if ( !definition.getList().isNumeric() ) {
			throw new IllegalArgumentException("Wrong codingScheme type for NumericCodeAttribute ");		
		}
	}
}
