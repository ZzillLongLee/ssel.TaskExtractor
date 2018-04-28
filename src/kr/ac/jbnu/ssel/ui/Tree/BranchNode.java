package kr.ac.jbnu.ssel.ui.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import kr.ac.jbnu.ssel.Configuration.Configuration;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;

public class BranchNode implements TreeNode {

	private String branchName;
	private String imagePath;
	private List<TaskNode> taskNodeList;
	private GitNode parent;
	private ImageRegistry imageRegistry;

	public BranchNode(GitNode gitNode, String branchName, List<Task> taskHistory) {
		this.parent = gitNode;
		this.branchName = branchName;
		this.imagePath = Configuration.BRANCH_IMAGE;
		this.taskNodeList = new ArrayList<>();
		for (Task task : taskHistory)
		{
			TaskNode taskNode = new TaskNode(this, task);
			taskNodeList.add(taskNode);
		}
	}

	@Override
	public String getName() {
		return branchName;
	}

	@Override
	public org.eclipse.swt.graphics.Image getImage() {
		return new Image(Display.getCurrent(), imagePath);
	}

	@Override
	public List getChildren() {
		// TODO Auto-generated method stub
		return taskNodeList;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		if (taskNodeList != null)
			return true;
		else
			return false;
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

}
