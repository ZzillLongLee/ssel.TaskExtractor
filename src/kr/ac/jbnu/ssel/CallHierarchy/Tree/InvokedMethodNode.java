package kr.ac.jbnu.ssel.CallHierarchy.Tree;

import java.util.List;

import gr.gousiosg.javacg.InvokedMethod.InvokedMethod;
import kr.ac.jbnu.ssel.ui.Tree.TreeNode;

public class InvokedMethodNode implements CallTreeNode {

	private MethodNode methodNode;
	private kr.ac.jbnu.ssel.CallHierarchy.Tree.MethodNode parentNode;
	private String methodType;
	private String format = "%s:%s(%s)";
	private String methodName;
	private List<InvokedMethodNode> invokedMethodNodes;

	public InvokedMethodNode(MethodNode methodNode, InvokedMethod invokedMethod) {
		this.parentNode = methodNode;
		this.methodType = invokedMethod.getInvokeType();
		this.methodName = String.format(format, invokedMethod.getReferenceType(), invokedMethod.getMethodName(),
				invokedMethod.getArgumentList());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return methodName;
	}

	@Override
	public List<InvokedMethodNode> getChildren() {
		return invokedMethodNodes;
	}

	@Override
	public boolean hasParent() {
		if (parentNode == null)
			return false;
		else
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
		// TODO Auto-generated method stub
		return (TreeNode) parentNode;
	}

	public void setMethodNode(MethodNode methodNode) {
		this.methodNode = methodNode;
		if (this.methodNode != null)
		{
			this.invokedMethodNodes = this.methodNode.getChildren();
		}
	}

}
