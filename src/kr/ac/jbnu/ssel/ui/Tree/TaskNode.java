package kr.ac.jbnu.ssel.ui.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import kr.ac.jbnu.ssel.Configuration.Configuration;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;

public class TaskNode implements TreeNode {

	private String taskName;
	private String imagePath;
	private List<ChangedFileNode> changedList;
	private BranchNode parent;
	private Display display;
	private ImageRegistry imageRegistry;

	public TaskNode(BranchNode branchNode, Task task) {
		// TODO Auto-generated constructor stub
		this.parent = branchNode;
		String taskName = task.getDate() + ": " + task.getAuthorName()+"("+ task.getCommitMsg()+")";
		this.taskName = taskName;
		this.imagePath = Configuration.TASK_IMAGE;

		changedList = new ArrayList<>();
		HashMap<String, String> changedFilesMap = task.getDiffFileChangedMap();
		HashMap<String, String> changedCodesMap = task.getChangedCodesMap();
		if (changedFilesMap != null)
		{
			for (Entry<String, String> changedFile : changedFilesMap.entrySet())
			{
				if (changedCodesMap != null)
				{
					String sourceCode = changedCodesMap.get(changedFile.getKey());
					if (null != sourceCode)
					{
						changedList.add(
								new ChangedFileNode(this, changedFile.getKey(), changedFile.getValue(), sourceCode));
					} else
						changedList.add(new ChangedFileNode(this, changedFile.getKey(), changedFile.getValue(), null));
				}
			}
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return taskName;
	}

	@Override
	public org.eclipse.swt.graphics.Image getImage() {
		return new Image(Display.getCurrent(), imagePath);
	}

	@Override
	public List getChildren() {
		// TODO Auto-generated method stub
		return changedList;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		if (changedList != null)
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
