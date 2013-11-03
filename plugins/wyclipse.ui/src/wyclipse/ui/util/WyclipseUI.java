package wyclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A bunch of handy methods to simplify laying out dialogs.
 * 
 * @author David J. Pearce
 * 
 */
public class WyclipseUI {
	
	public static Group createGroup(Composite parent, String text, int style, int horizontalSpan, int numColumns) {
		Group group = new Group(parent, style);
		group.setText(text);
		GridLayout layout = new GridLayout();		
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		group.setLayoutData(gd);
		layout.numColumns = numColumns;
		return group;
	}
	
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
	
	public static Combo createCombo(Composite parent, int horizontalSpan, String... items) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Combo combo = new Combo(parent, SWT.CHECK);
		for(String item : items) {
			combo.add(item);
		}
		combo.setLayoutData(gd);
		return combo;
	}
	
	public static Label createSeparator(Composite parent, int horizontalSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Label label = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		label.setLayoutData(gd);
		return label;
		
	}
	
	public static Label createLabel(Composite parent, String text, int horizontalSpan) {
		GridData gd = new GridData();
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
