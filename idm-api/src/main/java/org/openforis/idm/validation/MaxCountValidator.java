/**
 * 
 */
package org.openforis.idm.validation;

import java.util.List;

import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;

/**
 * @author G. Miceli
 * @author M. Togna
 */
public class MaxCountValidator implements Validator<Entity> {

	private NodeDefinition nodeDefinition;
	
	public MaxCountValidator(NodeDefinition nodeDefinition) {
		this.nodeDefinition = nodeDefinition;
	}

	public NodeDefinition getNodeDefinition() {
		return nodeDefinition;
	}
	
	@Override
	public boolean validate(Entity node) {
		String name = nodeDefinition.getName();
		Integer maxCount = nodeDefinition.getMaxCount();
		if (maxCount == null) {
			return true;
		} else {
			List<Node<?>> children = node.getAll(name);
			return children.size() <= maxCount;
		}
	}
}
