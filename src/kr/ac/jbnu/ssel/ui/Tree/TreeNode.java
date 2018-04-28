package kr.ac.jbnu.ssel.ui.Tree;

import java.util.List;

public interface TreeNode
{
	public String getName();
	public org.eclipse.swt.graphics.Image getImage();
	public List getChildren();
	public boolean hasChildren();
	public TreeNode getParent();
}
