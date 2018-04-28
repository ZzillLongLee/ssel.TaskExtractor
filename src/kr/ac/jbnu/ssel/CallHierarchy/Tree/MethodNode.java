package kr.ac.jbnu.ssel.CallHierarchy.Tree;

import java.util.ArrayList;
import java.util.List;

import gr.gousiosg.javacg.InvokedMethod.InvokedMethod;
import kr.ac.jbnu.ssel.ui.Tree.TreeNode;

public class MethodNode implements CallTreeNode {

	private String methodName;
	private List<InvokedMethodNode> invokedMethodNodes;

	public MethodNode(String methodName, List<InvokedMethod> invokedList) {
		this.methodName = methodName;
		this.invokedMethodNodes = new ArrayList<>();
		for (InvokedMethod invokedMethod : invokedList)
		{
			InvokedMethodNode invokedMethodNode = new InvokedMethodNode(this, invokedMethod);
			invokedMethodNodes.add(invokedMethodNode);
		}
	}

	@Override
	public String getName() {
		return methodName;
	}

	@Override
	public List<InvokedMethodNode> getChildren() {
		return invokedMethodNodes;
	}

	@Override
	public boolean hasParent() {
		return true;
	}

	@Override
	public boolean hasChildren() {
		if (invokedMethodNodes == null || invokedMethodNodes.size() == 0)
			return false;
		else
			return true;
	}

	@Override
	public TreeNode getParent() {
		return null;
	}
}
