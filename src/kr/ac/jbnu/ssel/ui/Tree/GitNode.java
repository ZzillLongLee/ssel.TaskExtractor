package kr.ac.jbnu.ssel.ui.Tree;

import org.eclipse.swt.graphics.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.dialogs.ViewLabelProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import kr.ac.jbnu.ssel.Configuration.Configuration;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;

public class GitNode implements TreeNode {
	private ImageRegistry imageRegistry;
	private String gitName;
	private ArrayList<BranchNode> branchList;
	private String imagePath;
	private ResourceManager resourceManager;

	public GitNode(HashMap<String, List<Task>> releaseUnits, String projectPath) {
		this.gitName = projectPath;
		this.imagePath = Configuration.GIT_IMAGE;
		this.branchList = new ArrayList<>();
		for (Entry<String, List<Task>> releaseUnit : releaseUnits.entrySet())
		{
			BranchNode branchNode = new BranchNode(this, releaseUnit.getKey(), releaseUnit.getValue());
			branchList.add(branchNode);
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return gitName;
	}

	@Override
	public org.eclipse.swt.graphics.Image getImage() {
		// TODO Auto-generated method stub
		return new Image(Display.getCurrent(), imagePath);
	}

	@Override
	public List getChildren() {
		// TODO Auto-generated method stub
		return branchList;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		if (branchList != null)
			return true;
		else
			return false;
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return null;
	}

}
