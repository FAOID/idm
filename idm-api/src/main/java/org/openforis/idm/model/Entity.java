/**
 * 
 */
package org.openforis.idm.model;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.FileAttributeDefinition;
import org.openforis.idm.metamodel.IdmInterpretationError;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.SurveyContext;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.metamodel.Unit;
import org.openforis.idm.metamodel.validation.ValidationResultFlag;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.expression.ExpressionFactory;
import org.openforis.idm.model.expression.InvalidExpressionException;
import org.openforis.idm.model.expression.RelevanceExpression;
import org.openforis.idm.model.expression.RequiredExpression;
import org.openforis.idm.util.CollectionUtil;

/**
 * @author G. Miceli
 * @author M. Togna
 * 
 */
public class Entity extends Node<EntityDefinition> {

	private static final long serialVersionUID = 1L;
	
	Map<String, ArrayList<Node<? extends NodeDefinition>>> childrenByName;
	private DerivedStateCache derivedStateCache;
	Map<String, State> childStates;
	
	public Entity(EntityDefinition definition) {
		super(definition);
		this.childrenByName = new HashMap<String, ArrayList<Node<? extends NodeDefinition>>>();
		this.derivedStateCache = new DerivedStateCache();
		this.childStates = new HashMap<String, State>();
	}

	@Override
	protected void setRecord(Record record) {
		super.setRecord(record);
		List<Node<?>> children = getChildren();
		for (Node<?> node : children) {
			node.setRecord(record);
		}
	}
	
	public void add(Node<?> node) {
		addInternal(node, null);
	}
	
	public void add(Node<?> node, int idx) {
		addInternal(node, idx);
	}
	
	public void setChildState(String childName, int intState) {
		State childState = getChildState(childName);
		childState.set(intState);
	}
	
	public void setChildState(String childName, int position, boolean value) {
		State childState = getChildState(childName);
		childState.set(position, value);
	}
	
	public State getChildState(String childName){
		checkChildDefinition(childName);
		
		State state = childStates.get(childName);
		if (state == null) {
			state = new State();
			childStates.put(childName, state);
		}
		return state;
	}
	
	/**
	 * @param name
	 * @return the newly created Entity
	 * @throws ArrayIndexOutOfBoundsException
	 *             if adding would break the maxCount rule
	 */
	public Entity addEntity(String name) {
		Entity entity = createEntity(name);
		addInternal(entity, null);
		return entity;
	}

	/**
	 * @param name
	 * @param idx
	 * @return the newly created Entity
	 * @throws ArrayIndexOutOfBoundsException
	 *             if adding would break the maxCount rule
	 */
	public Entity addEntity(String name, int idx) {
		Entity entity = createEntity(name);
		addInternal(entity, idx);
		return entity;
	}

	/**
	 * @return true if any descendant is a non-blank value
	 */
	@Override
	public boolean isEmpty() {
		Collection<ArrayList<Node<?>>> childLists = childrenByName.values();
		for (List<Node<?>> list : childLists) {
			for (Node<?> node : list) {
				if (!node.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean hasData() {
		List<Node<?>> children = getChildren();
		for ( Node<?> child : children ) {
			if( child.hasData() ){
				return true;
			}
		}
		return false;
	}

//	public <V extends Value> Attribute<?, ?> addValue(String name, V value) {
//		return addValueInternal(name, value, null);
//	}
//	
//	public <V extends Value> Attribute<?, ?> addValue(String name, V value, int idx) {
//		return addValueInternal(name, value, idx);
//	}
	
	public BooleanAttribute addValue(String name, Boolean value, int idx) {
		return addValueInternal(name, new BooleanValue(value), idx, BooleanAttribute.class, BooleanAttributeDefinition.class);
	}

	public BooleanAttribute addValue(String name, Boolean value) {
		return addValueInternal(name, new BooleanValue(value), null, BooleanAttribute.class, BooleanAttributeDefinition.class);
	}

	public CodeAttribute addValue(String name, Code value, int idx) {
		return addValueInternal(name, value, idx, CodeAttribute.class, CodeAttributeDefinition.class);
	}

	public CodeAttribute addValue(String name, Code value) {
		return addValueInternal(name, value, null, CodeAttribute.class, CodeAttributeDefinition.class);
	}

	public CoordinateAttribute addValue(String name, Coordinate value, int idx) {
		return addValueInternal(name, value, idx, CoordinateAttribute.class, CoordinateAttributeDefinition.class);
	}

	public CoordinateAttribute addValue(String name, Coordinate value) {
		return addValueInternal(name, value, null, CoordinateAttribute.class, CoordinateAttributeDefinition.class);
	}

	public FileAttribute addValue(String name, File value, int idx) {
		return addValueInternal(name, value, idx, FileAttribute.class, FileAttributeDefinition.class);
	}

	public FileAttribute addValue(String name, File value) {
		return addValueInternal(name, value, null, FileAttribute.class, FileAttributeDefinition.class);
	}

	public RealAttribute addValue(String name, Double value, Unit unit, int idx) {
		return addValueInternal(name, new RealValue(value, unit), idx, RealAttribute.class, NumberAttributeDefinition.class);
	}

	public RealAttribute addValue(String name, Double value, Unit unit) {
		return addValueInternal(name, new RealValue(value, unit), null, RealAttribute.class, NumberAttributeDefinition.class);
	}

	public IntegerAttribute addValue(String name, Integer value, Unit unit, int idx) {
		return addValueInternal(name, new IntegerValue(value, unit), idx, IntegerAttribute.class, NumberAttributeDefinition.class);
	}

	public IntegerAttribute addValue(String name, Integer value, Unit unit) {
		return addValueInternal(name, new IntegerValue(value, unit), null, IntegerAttribute.class, NumberAttributeDefinition.class);
	}

	public RealAttribute addValue(String name, Double value, int idx) {
		return addValueInternal(name, new RealValue(value, null), idx, RealAttribute.class, NumberAttributeDefinition.class);
	}

	public RealAttribute addValue(String name, Double value) {
		return addValueInternal(name, new RealValue(value, null), null, RealAttribute.class, NumberAttributeDefinition.class);
	}

	public IntegerAttribute addValue(String name, Integer value, int idx) {
		return addValueInternal(name, new IntegerValue(value, null), idx, IntegerAttribute.class, NumberAttributeDefinition.class);
	}

	public IntegerAttribute addValue(String name, Integer value) {
		return addValueInternal(name, new IntegerValue(value, null), null, IntegerAttribute.class, NumberAttributeDefinition.class);
	}

	public RealRangeAttribute addValue(String name, RealRange value, int idx) {
		return addValueInternal(name, value, idx, RealRangeAttribute.class, RangeAttributeDefinition.class);
	}

	public RealRangeAttribute addValue(String name, RealRange value) {
		return addValueInternal(name, value, null, RealRangeAttribute.class, RangeAttributeDefinition.class);
	}

	public IntegerRangeAttribute addValue(String name, IntegerRange value, int idx) {
		return addValueInternal(name, value, idx, IntegerRangeAttribute.class, RangeAttributeDefinition.class);
	}

	public IntegerRangeAttribute addValue(String name, IntegerRange value) {
		return addValueInternal(name, value, null, IntegerRangeAttribute.class, RangeAttributeDefinition.class);
	}
	
	public DateAttribute addValue(String name, Date value, int idx) {
		return addValueInternal(name, value, idx, DateAttribute.class, DateAttributeDefinition.class);
	}

	public DateAttribute addValue(String name, Date value) {
		return addValueInternal(name, value, null, DateAttribute.class, DateAttributeDefinition.class);
	}
	
	public TextAttribute addValue(String name, String value, int idx) {
		return addValueInternal(name, new TextValue(value), idx, TextAttribute.class, TextAttributeDefinition.class);
	}

	public TextAttribute addValue(String name, String value) {
		return addValueInternal(name, new TextValue(value), null, TextAttribute.class, TextAttributeDefinition.class);
	}

	public TaxonAttribute addValue(String name, TaxonOccurrence value, int idx) {
		return addValueInternal(name, value, idx, TaxonAttribute.class, TaxonAttributeDefinition.class);
	}

	public TaxonAttribute addValue(String name, TaxonOccurrence value) {
		return addValueInternal(name, value, null, TaxonAttribute.class, TaxonAttributeDefinition.class);
	}

	public TimeAttribute addValue(String name, Time value, int idx) {
		return addValueInternal(name, value, idx, TimeAttribute.class, TimeAttributeDefinition.class);
	}

	public TimeAttribute addValue(String name, Time value) {
		return addValueInternal(name, value, null, TimeAttribute.class, TimeAttributeDefinition.class);
	}

	public Node<? extends NodeDefinition> get(String name, int index) {
		checkChildDefinition(name);

		List<Node<? extends NodeDefinition>> list = childrenByName.get(name);
		if (list == null || index >= list.size()) {
			return null;
		} else {
			return list.get(index);
		}
	}

	public Object getValue(String name, int index) {
		Node<?> node = get(name, index);
		if ( node instanceof Attribute ) {
			return ((Attribute<?,?>) node).getValue();
		} else if ( node == null ) {
			return null;
		} else {
			throw new IllegalArgumentException("Child "+name+" not an attribute"); 
		}
	}

	private void checkChildDefinition(String name) {
		getChildDefinition(name);
	}

	/*
	 * public Set<String> getChildNames() { Set<String> childNames = childrenByName.keySet(); return Collections.unmodifiableSet(childNames); }
	 */
	public int getCount(String name) {
		checkChildDefinition(name);
		List<Node<?>> list = childrenByName.get(name);
		return list == null ? 0 : list.size();
	}
	
	public int getNonEmptyCount(String name) {
		checkChildDefinition(name);
		int count = 0;
		List<Node<?>> list = childrenByName.get(name);
		if(list != null && list.size() > 0) {
			for (Node<?> node : list) {
				if(! node.isEmpty()) {
					count ++;
				}
			}
		}
		return count;
	}
	
	public int getMissingCount(String name) {
		int minCount = getEffectiveMinCount(name);
		int specified = getNonEmptyCount(name);
		if(minCount > specified) {
			return minCount - specified;
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns the number of children
	 * @return
	 */
	public int size(){
		return childrenByName.size();
	}

	public void move(String name, int oldIndex, int newIndex) {
		List<Node<? extends NodeDefinition>> list = childrenByName.get(name);
		if (list != null) {
			Node<? extends NodeDefinition> obj = list.remove(oldIndex);
			list.add(newIndex, obj);
		}
		// updateList(newIndex, name);
	}

	public Node<? extends NodeDefinition> remove(String name, int index) {
		List<Node<? extends NodeDefinition>> list = childrenByName.get(name);
		if (list == null) {
			return null;
		} else {
			Node<? extends NodeDefinition> node = list.remove(index);
			node.parent = null;
			
			if( node instanceof Entity ) {
				Entity entity = (Entity)node;
				entity.traverse(new NodeVisitor() {
					@Override
					public void visit(Node<? extends NodeDefinition> node, int idx) {
						node.record = null;
					}
				});
			} else if ( node instanceof Attribute ){
				node.record = null;
			}
			// this.updateList(index, name);
			return node;
		}
	}

//	@SuppressWarnings("unchecked")
//	private <T extends Attribute<D, V>, D extends AttributeDefinition, V extends Value> T addValueInternal(String name, Object value, Integer idx) {
//		T attr = (T) createNode(name);
//		if ( value instanceof Value ) {
//			attr.setValue((V) value);
//		} else {
////			attr.setValue(value);
//		}
//		return addInternal(attr, idx);
//	}

	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private  Attribute<?, ?> addValueInternal(String name, Value value, Integer idx) {
//		Node<?> node = createNode(name);
//		if ( node instanceof Attribute ) {
//			Attribute attr = (Attribute) node;
//			attr.setValue((Value) value);
//			return addInternal(attr, idx);
//		} else {
//			throw new IllegalArgumentException("Wrong node type; '"+name+"' is not an attribute");
//		}
//	}
	
	private <T extends Attribute<D, V>, D extends AttributeDefinition, V extends Value> T addValueInternal(String name, V value, Integer idx, Class<T> type, Class<D> definitionType) {
		T attr = createNode(name, type, definitionType);
		addInternal(attr, idx);
		attr.setValue(value);
		return attr;
	}

	/**
	 * Adds an item at the specified index. Assumed o has already been checked to be of the appropriate type. All added entities or attributes pass
	 * through this method
	 * 
	 * @param o
	 * @param idx
	 * @throws ArrayIndexOutOfBoundsException
	 *             if inserting object would break the maxCount rule
	 */
	private <T extends Node<?>> T addInternal(T o, Integer idx) {
//		Record record = getRecord();
//		if (record == null) {
//			throw new DetachedNodeDefinitionException(o.getDefinition().getClass(), this.getClass());
//		}

		// Get child definition and name
		NodeDefinition defn = o.getDefinition();
		String name = defn.getName();

		// Get child's definition and check schema object definition is the same
		NodeDefinition childDefn = getDefinition().getChildDefinition(name);
		if ( childDefn == null ) {
			throw new IllegalArgumentException("'"+name+"' not allowed in '"+getName()+"'");			
		} else if (defn != childDefn) {
			throw new IllegalArgumentException("'"+name+"' in '"+getName()+"' wrong type");
		}

		// Get or create list containing children
		ArrayList<Node<? extends NodeDefinition>> children = childrenByName.get(name);
		if (children == null) {
			children = new ArrayList<Node<? extends NodeDefinition>>();
			childrenByName.put(name, children);
		}

//		 Check maxCount constraint
//		int cnt = children.size();
//		Integer max = defn.getMaxCount();
//		if (max != null && cnt >= max) {
//			throw new ArrayIndexOutOfBoundsException("At most " + max + " " + defn.getPath() + " allowed");
//		}

		// Add item
		if (idx == null) {
			children.add(o);
		} else {
			children.add(idx, o);
		}

		o.setParent(this);

		if ( !isDetached() ) {
			o.clearDependencyStates();
		}
		
		return o;
	}

//	private Node<?> createNode(String name) {
//		NodeDefinition definition = getChildDefinition(name);
//		
//		return definition.createNode();		
//	}
	
	private <T extends Node<D>, D extends NodeDefinition> T createNode(String name, Class<T> type, Class<D> definitionType) {
		try {
			NodeDefinition definition = getChildDefinition(name, definitionType);
			return type.getConstructor(definitionType).newInstance(definition);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private Entity createEntity(String name) {
		EntityDefinition defn = getChildDefinition(name, EntityDefinition.class);
		Entity entity = new Entity(defn);
		return entity;
	}

	/**
	 * Get child definition
	 * 
	 * @throws IllegalArgumentException
	 *             if not defined in model
	 */
	private NodeDefinition getChildDefinition(String name) {
		NodeDefinition childDefinition = getDefinition().getChildDefinition(name);
		if (childDefinition == null) {
			throw new IllegalArgumentException("Undefined schema object " + getDefinition().getPath() + "/" + name);
		}
		return childDefinition;
	}

	/**
	 * Get child definition and cast to requested type
	 * 
	 * @throws IllegalArgumentException
	 *             if not defined in model or if not assignable from type defined in definitionClass
	 */
	@SuppressWarnings("unchecked")
	private <T> T getChildDefinition(String name, Class<T> definitionClass) {
		NodeDefinition childDefinition = getChildDefinition(name);
		if (!childDefinition.getClass().isAssignableFrom(definitionClass)) {
			throw new IllegalArgumentException(childDefinition.getPath() + " is not a " + definitionClass.getSimpleName());
		}
		return (T) childDefinition;
	}

	public List<Node<? extends NodeDefinition>> getAll(String name) {
		List<Node<? extends NodeDefinition>> children = childrenByName.get(name);
		return  CollectionUtil.unmodifiableList(children);
	}

	//
	// public void clear(String name) {
	// this.children.remove(name);
	// }
	//
	//
	// public void clear() {
	// this.children.clear();
	// }

	//
	// public Node<? extends NodeDefinition> set(Node<? extends NodeDefinition> o, int index) {
	// this.beforeUpdate(o);
	// String name = o.getDefinition().getName();
	// List<Node<? extends NodeDefinition>> list = this.get(name);
	// Node<? extends NodeDefinition> object = list.set(index, o);
	//
	// this.updateList(index, name);
	//
	// return object;
	// }

	/*
	 * private void beforeUpdate(Node<? extends NodeDefinition> o) { ((Node<? extends NodeDefinition>) o).setRecord(this.getRecord()); }
	 * 
	 * private void updateList(int fromIndex, String name) { List<Node<? extends NodeDefinition>> list = this.children.get(name); for (int i =
	 * fromIndex; i < list.size(); i++) { Node<? extends NodeDefinition> node = (Node<? extends NodeDefinition>) list.get(i);
	 * node.setPath(this.getPath() + "/" + name + "[" + i + "]"); } }
	 */

	@Override
	protected void write(StringWriter sw, int indent) {
		for (int i = 0; i < indent; i++) {
			sw.append('\t');
		}
		sw.append(getName());
		sw.append(":\n");
		// List<Node<? extends NodeDefinition>> children = getChildren();
		// for (Node<? extends NodeDefinition> child : children) {
		List<NodeDefinition> definitions = getDefinition().getChildDefinitions();
		for (NodeDefinition defn : definitions) {
			List<Node<? extends NodeDefinition>> children = childrenByName.get(defn.getName());
			if (children != null) {
				for (Node<? extends NodeDefinition> child : children) {
					child.write(sw, indent + 1);
				}
			}
		}
		// }
	}

	// Pre-order depth-first traversal from here down
	public void traverse(NodeVisitor visitor) {
		// Initialize stack with root entity
		NodeStack stack = new NodeStack(this);
		// While there are still nodes to insert
		while (!stack.isEmpty()) {
			// Pop the next list of nodes to insert
			List<Node<? extends NodeDefinition>> nodes = stack.pop();
			// Insert this list in order
			for (int i = 0; i < nodes.size(); i++) {
				Node<? extends NodeDefinition> node = nodes.get(i);

				if ( node == null ) {
					throw new IllegalStateException("Null node in entity children list");
				}
				visitor.visit(node, i);

				// For entities, add existing child nodes to the stack
				if (node instanceof Entity) {
					Entity entity = (Entity) node;
					EntityDefinition defn = entity.getDefinition();
					List<NodeDefinition> childDefns = defn.getChildDefinitions();
					for (NodeDefinition childDefn : childDefns) {
						String childName = childDefn.getName();
						List<Node<? extends NodeDefinition>> children = entity.getAll(childName);
						if (children != null) {
							stack.push(children);
						}
					}
				}
			}
		}
	}

	private class NodeStack extends Stack<List<Node<? extends NodeDefinition>>> {
		private static final long serialVersionUID = 1L;

		public NodeStack(Entity root) {
			ArrayList<Node<? extends NodeDefinition>> rootList = new ArrayList<Node<? extends NodeDefinition>>(1);
			rootList.add(root);
			push(rootList);
		}
	}

	public boolean isRelevant(String childName) {
		Boolean relevant = derivedStateCache.getRelevance(childName);
		if ( relevant == null ) {
			relevant = evaluateRelevance(childName);
			derivedStateCache.setRelevance(childName, relevant);
		}
		return relevant;
	}

	private boolean evaluateRelevance(String childName) {
		EntityDefinition parentDefn = (EntityDefinition) getDefinition().getParentDefinition();
		 
		if(parentDefn == null || parent == null || parent.isRelevant(getName())){
			NodeDefinition defn = getChildDefinition(childName);
			String expr = defn.getRelevantExpression();
			if (StringUtils.isBlank(expr)) {
				return true;
			} else {
				try {
					ExpressionFactory expressionFactory = getExpressionFactory();
					RelevanceExpression relevanceExpr = expressionFactory.createRelevanceExpression(expr);
					return relevanceExpr.evaluate(this, null);
				} catch (InvalidExpressionException e) {
					throw new IdmInterpretationError("Unable to evaluate expression: " + expr, e);
				} catch(Exception e){
					throw new IdmInterpretationError("Unable to evaluate expression: " + expr, e);
				}
			}
		} else {
			return false;
		}
	}

	public boolean isRequired(String childName) {
		NodeDefinition defn = getChildDefinition(childName);
		Integer minCount = defn.getMinCount();
		if ( minCount == null ) {
			String requiredExpression = defn.getRequiredExpression();
			if ( StringUtils.isBlank(requiredExpression) ) {
				return false;
			} else {
				Boolean required = derivedStateCache.getRequired(childName);
				if ( required == null ) {
					required = evaluateRequiredExpression(childName);
					derivedStateCache.setRequired(childName, required);
				}
				return required;
			}
		} else {
			return minCount > 0;
		}
	}

	private boolean evaluateRequiredExpression(String childName) {
		try {
			NodeDefinition defn = getChildDefinition(childName);
			Record record = getRecord();
			SurveyContext context = record.getSurveyContext();
			ExpressionFactory expressionFactory = context.getExpressionFactory();
			String requiredExpression = defn.getRequiredExpression();
			RequiredExpression expr = expressionFactory.createRequiredExpression(requiredExpression);
			return expr.evaluate(this, null);
		} catch (InvalidExpressionException e) {
			throw new IdmInterpretationError("Error evaluating required expression", e);
		}
	}

	/**
	 * 
	 * @param childName
	 * @return minimum number of non-empty child nodes, based on minCount, required and 
	 * requiredExpression properties
	 */
	public int getEffectiveMinCount(String childName) {
		NodeDefinition defn = getChildDefinition(childName);
		Integer minCount = defn.getMinCount();
		// requiredExpression is only considered if minCount and required are not set
		if ( minCount==null ) {
			return isRequired(childName) ? 1 : 0;
		} else {
			return minCount;
		}
	}

	public ValidationResultFlag validateMinCount(String childName) {
		SurveyContext ctx = getContext();
		Validator v = ctx.getValidator();
		return v.validateMinCount(this, childName);
	}
	
	public ValidationResultFlag validateMaxCount(String childName) {
		SurveyContext ctx = getContext();
		Validator v = ctx.getValidator();
		return v.validateMaxCount(this, childName);
	}
	// /**
	// *
	// * @return Unmodifiable list of child instances, sorted by their schema order.
	// */
	// public List<Node<? extends NodeDefinition>> getChildren() {
	// List<Node<? extends NodeDefinition>> result = new ArrayList<Node<? extends NodeDefinition>>();
	// List<NodeDefinition> definitions = getDefinition().getChildDefinitions();
	// for (NodeDefinition defn : definitions) {
	// List<Node<? extends NodeDefinition>> children = childrenByName.get(defn.getName());
	// if ( children != null ) {
	// for (Node<? extends NodeDefinition> child : children) {
	// result.add(child);
	// }
	// }
	// }
	// return Collections.unmodifiableList(result);
	// }
	
	public void clearRelevanceState(String childName) {
		derivedStateCache.clearRelevance(childName);
	}

	public void clearRequiredState(String childName) {
		derivedStateCache.clearRequiredState(childName);
	}
	
	private static class DerivedStateCache {
		/** Set of children dynamic required states */
		private Map<String, Boolean> childRequiredStates;
		/** Set of children relevance states */
		private Map<String, Boolean> childRelevance;

		public DerivedStateCache() {
			childRequiredStates = new HashMap<String, Boolean>();
			childRelevance = new HashMap<String, Boolean>();
		}

		private Boolean getRequired(String childName) {
			return childRequiredStates.get(childName);
		}

		private Boolean getRelevance(String childName) {
			return childRelevance.get(childName);
		}

		private void setRequired(String childName, boolean flag) {
			childRequiredStates.put(childName, flag);
		}

		private void clearRequiredState(String childName) {
			childRequiredStates.remove(childName);
		}

		private void setRelevance(String childName, boolean flag) {
			childRelevance.put(childName, flag);
		}

		private void clearRelevance(String childName) {
			childRelevance.remove(childName);
		}
	}

	/**
	 * 
	 * @return Unmodifiable list of child instances, sorted by their schema
	 *         order.
	 */
	public List<Node<? extends NodeDefinition>> getChildren() {
		List<Node<? extends NodeDefinition>> result = new ArrayList<Node<? extends NodeDefinition>>();
		List<NodeDefinition> definitions = getDefinition().getChildDefinitions();
		for (NodeDefinition defn : definitions) {
			List<Node<? extends NodeDefinition>> children = childrenByName.get(defn.getName());
			if (children != null) {
				for (Node<? extends NodeDefinition> child : children) {
					result.add(child);
				}
			}
		}
		return Collections.unmodifiableList(result);
	}

	public void clearChildStates() {
		this.childStates = new HashMap<String, State>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childStates == null) ? 0 : childStates.hashCode());
		result = prime * result
				+ ((childrenByName == null) ? 0 : childrenByName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (childStates == null) {
			if (other.childStates != null)
				return false;
		} else if (!childStates.equals(other.childStates))
			return false;
		if (childrenByName == null) {
			if (other.childrenByName != null)
				return false;
		} else if (!childrenByName.equals(other.childrenByName))
			return false;
		return true;
	}
}
