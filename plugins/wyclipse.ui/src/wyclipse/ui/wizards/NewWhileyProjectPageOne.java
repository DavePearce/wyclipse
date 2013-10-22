// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyclipse.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Implements the first page of the new Whiley project wizard. The main
 * responsibilities of this page are:
 * <ol>
 * <li>Determine the project, and ensure this does not already exist.</li>
 * <li>Determine the project location, using the default location as the initial
 * value.</li>
 * </ol>
 * 
 * @author David J. Pearce
 * 
 */
public class NewWhileyProjectPageOne extends WizardNewProjectCreationPage {
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewWhileyProjectPageOne() {
		super("Create a Whiley Project");
		setTitle("Create a Whiley Project");
		setDescription("Enter a project name.");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);		
		Composite container = (Composite) getControl();
		// at this point, we can use container to add more widgets onto the main
		// page. For now, I don't do anything. 
		
//		GridLayout layout = new GridLayout();		
//		container.setLayout(layout);
//		layout.numColumns = 3;
//		layout.verticalSpacing = 9;	
//		layout.marginWidth = 20;
//		Label label = new Label(container, SWT.NULL);
//		label.setText("&Project Name:");
//
//		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = 2;
//		projectName.setLayoutData(gd);
//		
//		defaultLocation = new Button(container, SWT.CHECK);				
//		defaultLocation.setText("Use default location");
//
//		location = new Text(container, SWT.BORDER | SWT.SINGLE);
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		location.setLayoutData(gd);
//		initialiseLocation();
//		
//		Button button = new Button(container, SWT.PUSH);
//		button.setText("Browse...");
//		button.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				handleBrowseLocation();
//			}
//		});
//		
//		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
//		
//		setControl(container);
	}
	
	public IWizardPage getNextPage() {
		System.out.println("GET NEXT PAGE - " + getLocationURI());
		return super.getNextPage();
	}
	
	public void dispose() {
		System.out.println("DISPOSE CALLED");
		super.dispose();
	}
	
	@Override
	public boolean validatePage() {
		System.out.println("VALIDATE PAGE");
		return super.validatePage();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		System.out.println("CAN FLIP TO NEXT PAGE");
		return super.validatePage();
	}
}