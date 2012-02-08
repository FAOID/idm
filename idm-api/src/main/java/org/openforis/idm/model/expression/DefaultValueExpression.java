/**
 * 
 */
package org.openforis.idm.model.expression;

import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.expression.internal.ModelJXPathCompiledExpression;
import org.openforis.idm.model.expression.internal.ModelJXPathContext;

/**
 * @author M. Togna
 * 
 */
public class DefaultValueExpression extends AbstractExpression {

	protected DefaultValueExpression(ModelJXPathCompiledExpression expression, ModelJXPathContext context) {
		super(expression, context);
	}

	public Object evaluate(Node<? extends NodeDefinition> context) throws InvalidPathException {
		try {
			return super.evaluateSingle(context);
		} catch (MissingValueException e) {
			return null;
		}
	}

}
