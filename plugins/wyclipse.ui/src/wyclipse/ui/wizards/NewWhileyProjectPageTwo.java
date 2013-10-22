package wyclipse.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import wybs.util.Trie;
import wyc.lang.WhileyFile;
import wyclipse.core.WhileyNature;
import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.pages.WhileyPathConfigurationControl;
import wyclipse.ui.util.WhileyPathViewer;
import wyil.lang.WyilFile;

public class NewWhileyProjectPageTwo extends WizardPage {
	protected NewWhileyProjectPageOne page1;
	protected WhileyPath whileypath;
	
	protected NewWhileyProjectPageTwo(NewWhileyProjectPageOne page1) {
		super("Whiley Project Settings");
		setTitle("Whiley Project Settings");
		setDescription("Configure the Whiley Build Path");
		this.page1 = page1;
	}

	public WhileyPath getWhileyPath() {
		return whileypath;
	}
	
	@Override
	public void createControl(Composite parent) {
		this.whileypath = detectWhileyPath();
		IProject project = page1.getProjectHandle(); 
				
		WhileyPathConfigurationControl wpControl = new WhileyPathConfigurationControl(
				getShell(), project, whileypath);
		Composite composite = wpControl.create(parent);

		setControl(composite);
	}
	
	protected void handleBrowseLocation() {
		
	}
	
	// ======================================================================
	// WhileyPath Helpers
	// ======================================================================

	/**
	 * Determine an appropriate initial whileypath. This is done by first
	 * checking whether there is already a whileypath; if not, we attempt to
	 * detect the appropriate whileypath; finally, we fall back to a default;
	 * 
	 * @return
	 */
	protected WhileyPath detectWhileyPath() {
		return WhileyNature.getDefaultWhileyPath();
	}
}
