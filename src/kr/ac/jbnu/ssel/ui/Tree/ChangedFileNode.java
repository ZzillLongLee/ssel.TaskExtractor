package kr.ac.jbnu.ssel.ui.Tree;

import java.util.List;

import javax.swing.ImageIcon;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import kr.ac.jbnu.ssel.Configuration.Configuration;

public class ChangedFileNode implements TreeNode {

	private String filePath;
	private String changedContext;
	private String sourceCode;
	private String imagePath;
	private TaskNode parent;
	private ImageRegistry imageRegistry;

	public ChangedFileNode(TaskNode taskNode, String filePath, String changedContext, String sourceCode) {
		this.parent = taskNode;
		this.filePath = filePath;
		this.changedContext = changedContext;
		this.sourceCode = sourceCode;
		this.imagePath = Configuration.CHANGEDFILE_IMAGE;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return filePath;
	}

	@Override
	public org.eclipse.swt.graphics.Image getImage() {
		return new Image(Display.getCurrent(), imagePath);
	}

	@Override
	public List getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	public String getChangedContext() {
		return changedContext;
	}


	public String getSourceCode() {
		return sourceCode;
	}

	
}
