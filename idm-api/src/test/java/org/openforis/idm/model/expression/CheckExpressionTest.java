/**
 * 
 */
package org.openforis.idm.model.expression;

import junit.framework.Assert;

import org.junit.Test;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.Record;

/**
 * @author M. Togna
 * 
 */
public class CheckExpressionTest extends AbstractExpressionTest {

	@Test
	public void testTrue() throws InvalidExpressionException {
		Record record = getRecord();
		Entity cluster = record.getRootEntity();
		RealAttribute plotDirection = (RealAttribute) cluster.get("plot_direction", 0);
		plotDirection.setValue(345.45);

		String expr = "$this >= 0 and $this <= 359";
		boolean b = evaluateExpression(expr, plotDirection);
		Assert.assertTrue(b);
	}

	@Test
	public void testFalse() throws InvalidExpressionException {
		Record record = getRecord();
		Entity cluster = record.getRootEntity();
		RealAttribute plotDirection = (RealAttribute) cluster.get("plot_direction", 0);
		plotDirection.setValue(385.45);

		String expr = "$this >= 0 and $this <= 359";
		boolean b = evaluateExpression(expr, plotDirection);
		Assert.assertFalse(b);
	}

	@Test
	public void testDefaultWithMissingNode() throws InvalidExpressionException {
		Record record = getRecord();
		Entity cluster = record.getRootEntity();
		RealAttribute plotDirection = (RealAttribute) cluster.get("plot_direction", 0);
		plotDirection.setValue(345.45);

		String expr = "../missing_attr >= 0 and $this <= 359";
		boolean b = evaluateExpression(expr, plotDirection);
		Assert.assertTrue(b);
	}

	private boolean evaluateExpression(String expr, Node<? extends NodeDefinition> context) throws InvalidExpressionException {
		CheckExpression expression = getRecordContext().getExpressionFactory().createCheckExpression(expr);
		boolean b = expression.evaluate(context);
		return b;
	}
}
