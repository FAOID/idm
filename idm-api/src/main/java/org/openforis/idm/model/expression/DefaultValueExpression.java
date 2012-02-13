/**
 * 
 */
package org.openforis.idm.model.expression;

import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.expression.internal.ModelJXPathCompiledExpression;
import org.openforis.idm.model.expression.internal.ModelJXPathContext;

/**
 * @author M. Togna
 * @author G. Miceli
 */
public class DefaultValueExpression extends AbstractExpression {

	protected DefaultValueExpression(ModelJXPathCompiledExpression expression, ModelJXPathContext context) {
		super(expression, context);
	}

	public Object evaluate(Node<?> node) throws InvalidPathException {
		try {
			Entity parent = node.getParent();
			return evaluateSingle(parent, null);
		} catch (MissingValueException e) {
			return null;
		}
	}
}
