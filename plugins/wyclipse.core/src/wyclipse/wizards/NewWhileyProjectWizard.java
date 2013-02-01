package wyclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


public class NewWhileyProjectWizard extends Wizard implements INewWizard {

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPages() {
		addPage(new NewProjectPage());	
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}
}
