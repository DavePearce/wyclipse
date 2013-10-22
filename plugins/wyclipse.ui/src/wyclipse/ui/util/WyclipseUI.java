package wyclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A bunch of handy methods to simplify laying out dialogs.
 * 
 * @author David J. Pearce
 * 
 */
public class WyclipseUI {
	
	public static Button createButton(Composite parent, String text, int width) {
		GridData gd = new GridData();
		gd.widthHint = width;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setLayoutData(gd);
		return button;
	}
	
	public static Button createCheckBox(Composite parent, String text, int horizontalSpan) {
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setLayoutData(gd);
		return button;
	}
	
	public static Label createSeparator(Composite parent, int horizontalSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Label label = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		label.setLayoutData(gd);
		return label;
		
	}
	
	public static Label createLabel(Composite parent, String text, int horizontalSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Label label = new Label(parent, SWT.NULL);
		label.setText(text);
		label.setLayoutData(gd);
		return label;
		
	}
	
	public static Text createText(Composite parent, String initialText, int horizontalSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText(initialText);
		text.setLayoutData(gd);
		return text;
	}
	
	public static Text createText(Composite parent, String initialText, int horizontalSpan, int width) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		gd.widthHint = width;
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText(initialText);
		text.setLayoutData(gd);
		return text;
	}
}
