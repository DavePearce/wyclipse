package wyclipse.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


public class NewWhileyProjectWizard extends Wizard implements INewWizard {
	private NewProjectPage page1;
	
	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPages() {
		page1 = new NewProjectPage();
		addPage(page1);	
	}

	@Override
	public boolean performFinish() {
		final String projectName = page1.getProjectName();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(projectName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		return true;
	}
	
	private void doFinish(String projectName, IProgressMonitor monitor) throws CoreException {
		
	}
}
