package kr.ac.jbnu.ssel.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SourceCodeDialog extends Dialog {

	private String sourceCode;

	public SourceCodeDialog(Shell parentShell, String sourceCode) {
		super(parentShell);
		this.sourceCode = sourceCode;
	}
	// , String sourceCode

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Source Code for Changed File");
		container.setLayout(new FormLayout());
		openSourceViewer(container);
		// Text text = new Text(container, SWT.None);
		// text.setText("Some text data");
		// txtData.width = 100;
		// txtData.height = 20;
		// txtData.left = new FormAttachment(0, 1000, 50);// x co-ordinate
		// txtData.top = new FormAttachment(0, 1000, 17);// y co-ordinate
		return container;

	}

	private void openSourceViewer(Composite parent) {
		SourceViewer sourceViewer;
		Document document;
		AnnotationModel annotationModel;
		SourceViewerConfiguration sourceViewerConfiguration;

		FormData sourceCodeData = new FormData();
		sourceCodeData.width = 400;
		sourceCodeData.height = 400;
		sourceCodeData.left = new FormAttachment(0, 1000, 50);// x co-ordinate
		sourceCodeData.top = new FormAttachment(0, 1000, 17);// y co-ordinate

		sourceViewer = new SourceViewer(parent, null, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		sourceViewerConfiguration = new SourceViewerConfiguration();
		sourceViewer.configure(sourceViewerConfiguration);

		// We need to handle how to show the source Code to use tableColumn
		document = new Document(sourceCode);
		annotationModel = new AnnotationModel();
		annotationModel.connect(document);

		sourceViewer.setDocument(document, annotationModel);
		sourceViewer.getTextWidget().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
		sourceViewer.getControl().setLayoutData(sourceCodeData);
		sourceViewer.setEditable(false);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Exit", true);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

}
