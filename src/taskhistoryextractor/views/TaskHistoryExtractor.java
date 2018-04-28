package taskhistoryextractor.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.*;

import kr.ac.jbnu.ssel.CallHierarchy.CallHirarchyExtractor;
import kr.ac.jbnu.ssel.Configuration.Configuration;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.releaseExtract.GitReleaseExtractor;
import kr.ac.jbnu.ssel.ui.Tree.ChangedFileNode;
import kr.ac.jbnu.ssel.ui.Tree.GitNode;
import kr.ac.jbnu.ssel.ui.TreeTableProvider.TreeContentProvider;
import kr.ac.jbnu.ssel.ui.TreeTableProvider.TreeLabelProvider;
import kr.ac.jbnu.ssel.ui.dialog.SourceCodeDialog;

import org.eclipse.jface.viewers.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.core.runtime.IAdaptable;
import javax.inject.Inject;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class TaskHistoryExtractor extends ViewPart {
	private TreeViewer treeViewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	@Override
	public void createPartControl(Composite parent) {
		try
		{
			treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

			GitReleaseExtractor gitReleaseExtractor = new GitReleaseExtractor();
			HashMap<String, List<Task>> releaseUnits = gitReleaseExtractor.releaseUnitsExtract(Configuration.GIT_URL);

			treeViewer.setContentProvider(new TreeContentProvider());
			treeViewer.setLabelProvider(new TreeLabelProvider());
			treeViewer.setInput(new GitNode(releaseUnits, Configuration.GIT_URL));

			makeActions(parent);
			hookContextMenu();
			hookDoubleClickAction();
			
			gitReleaseExtractor.getCommitProejctsAsJar(releaseUnits);
			
		} catch (

		IOException | GitAPIException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskHistoryExtractor.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions(Composite parent) {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = treeViewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof ChangedFileNode)
				{
					String sourceCode = ((ChangedFileNode) obj).getSourceCode();
					if (sourceCode != null)
						new SourceCodeDialog(parent.getShell(), sourceCode).open();
					else
						showMessage("sourceCode is null");
				} else
					showMessage("Double-click detected on " + obj.toString());
			}

		};
	}

	private void hookDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(treeViewer.getControl().getShell(), "TaskHistoryExtractorView", message);
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
}
