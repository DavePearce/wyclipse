package wyclipse.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import wyclipse.Activator;
import wyclipse.WhileyCore;


public class NewWhileyProjectWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	
	private IConfigurationElement config;
	
	private NewWhileyProjectCreationPage page1;
	
	private IProject project;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	@Override
	public void addPages() {
		page1 = new NewWhileyProjectCreationPage();
		addPage(page1);	
	}

	public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        this.config = config;
    }
	
	@Override
	public boolean performFinish() {
		if (project != null) {
            return true;
        } 

		final IProject projectHandle = page1.getProjectHandle();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// Configure project location
		URI projectURI = (!page1.useDefaults()) ? page1.getLocationURI() : null;

        final IProjectDescription desc = workspace
                .newProjectDescription(projectHandle.getName());

        desc.setLocationURI(projectURI);

        // Create project
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor)
                    throws CoreException {
                createProject(desc, projectHandle, monitor);
            }
        };

        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException
                    .getMessage());
            return false;
        }

        project = projectHandle;

        if (project == null) {
            return false;
        }

        BasicNewProjectResourceWizard.updatePerspective(config);
        BasicNewProjectResourceWizard.selectAndReveal(project, workbench
                .getActiveWorkbenchWindow());

        return true;
	}
	
	/**
	 * Responsible for actually creating the given project and populating its
	 * description. One of the responsibilities of this function is to configure
	 * the project description with the appropriate Whiley nature and builder.
	 * 
	 * @param description
	 * @param proj
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(IProjectDescription description, IProject proj,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {

		try {

            monitor.beginTask("Creating Whiley Project", 2000);

			// First, add Whiley Nature onto list of natures
			String[] natures = new String[] { Activator.WYCLIPSE_NATURE_ID };
			description.setNatureIds(natures);

			// Second, add Whiley Builder onto list of builders
			ICommand buildCommand = description.newCommand();				
			buildCommand.setBuilderName(Activator.WYCLIPSE_BUILDER_ID);

			ICommand[] newBuilders = new ICommand[1];
			newBuilders[0] = buildCommand;
			description.setBuildSpec(newBuilders);		
			
			// done --- create project
            proj.create(description, new SubProgressMonitor(monitor, 1000));

            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
                    monitor, 1000));
            
		} finally {
			monitor.done();
		}
	}            
}
