package kr.ac.jbnu.ssel.CallHierarchy.Tree;

import java.util.List;

import gr.gousiosg.javacg.InvokedMethod.InvokedMethod;
import kr.ac.jbnu.ssel.ui.Tree.TreeNode;

public interface CallTreeNode {
	public String getName();
	public List<InvokedMethodNode> getChildren();
	public boolean hasParent();
	public boolean hasChildren();
	public TreeNode getParent();
}
