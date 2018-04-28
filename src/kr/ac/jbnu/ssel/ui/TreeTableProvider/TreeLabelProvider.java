package kr.ac.jbnu.ssel.ui.TreeTableProvider;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;

import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;
import kr.ac.jbnu.ssel.ui.Tree.TreeNode;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

public class TreeLabelProvider extends LabelProvider{

	public String getText(Object element) {		
		return ((TreeNode)element).getName();
	}
	
	public Image getImage(Object element) {		
		return ((TreeNode)element).getImage();
	}

}
