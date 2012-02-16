/**
 * 
 */
package org.openforis.idm.model.expression;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.Record;

/**
 * @author M. Togna
 * 
 */
public class ModelPathExpressionTest extends AbstractExpressionTest {

	@Test
	public void testIteratePath() throws InvalidExpressionException {
		Record record = getRecord();
		Entity cluster = record.getRootEntity();

		String expr = "plot";
		List<Node<?>> list = iterateExpression(expr, cluster);

		Assert.assertEquals(3, list.size());
	}

	@Test(expected = InvalidExpressionException.class)
	public void testIterateInvalidPath() throws InvalidExpressionException {
		Record record = getRecord();
		Entity entity = record.getRootEntity();

		String expr = "plot^2";
		List<Node<?>> list = iterateExpression(expr, entity);

		Assert.assertEquals(3, list.size());
	}

	@Test
	public void testIteratePath2() throws InvalidExpressionException {
		Record record = getRecord();
		Entity entity = record.getRootEntity();

		String expr = "plot/no";
		List<Node<?>> list = iterateExpression(expr, entity);

		Assert.assertEquals(3, list.size());
	}

	@Test
	public void testIteratePath3() throws InvalidExpressionException {
		Record record = getRecord();
		Entity entity = record.getRootEntity();

		String expr = "time_study";
		List<Node<?>> list = iterateExpression(expr, entity);

		Assert.assertEquals(2, list.size());
	}

//	@SuppressWarnings("unused")
//	private Object evaluateExpression(String expr, Node<? extends NodeDefinition> context) throws InvalidExpressionException {
//		ModelPathExpression expression = getRecordContext().getExpressionFactory().createModelPathExpression(expr);
//		Object o = expression.evaluate(context);
//		return o;
//	}

	private List<Node<?>> iterateExpression(String expr, Node<? extends NodeDefinition> context) throws InvalidExpressionException {
		ModelPathExpression expression = getRecordContext().getExpressionFactory().createModelPathExpression(expr);
		List<Node<?>> l = expression.iterate(context, null);
		return l;
	}
}