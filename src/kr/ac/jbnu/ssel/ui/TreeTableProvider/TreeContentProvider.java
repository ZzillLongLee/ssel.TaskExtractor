package kr.ac.jbnu.ssel.ui.TreeTableProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;
import kr.ac.jbnu.ssel.ui.Tree.TreeNode;

public class TreeContentProvider implements ITreeContentProvider
{

	@Override
	public Object[] getChildren(Object element) {
		// TODO Auto-generated method stub
		return ((TreeNode)element).getChildren().toArray();
	}

	@Override
	public Object[] getElements(Object element) {
		// TODO Auto-generated method stub
		return getChildren(element);
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return ((TreeNode)element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return ((TreeNode)element).hasChildren();
	}

}