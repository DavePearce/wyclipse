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

package wyclipse.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import wybs.lang.Content;
import wybs.util.Trie;
import wyc.lang.WhileyFile;
import wyclipse.core.builder.WhileyPath;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wyil.lang.WyilFile;

/**
 * <p>Represents the fundamental building block of the Wyclipse plugin. Attaching a
 * Whiley Nature to a project gives it the ability to compile and execute Whiley
 * files. The Whiley nature reads its build configuration from the
 * ".whileypath", which operates in a similar fashion as for JDT's ".classpath".</p>
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyNature implements IProjectNature {

	/**
	 * The following describe the persistent properties maintained for the
	 * Whiley Compiler. These are user configurable parameters which control the
	 * compilation process. For example, verification can be enabled or disabled
	 * through the "Whiley Compiler" property page.
	 */
	public static final QualifiedName VERIFICATION_PROPERTY = new QualifiedName(
			"", "VERIFICATION");
	
	public static final boolean VERIFICATION_DEFAULT = true;

	private IProject project;
				
	@Override
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		String[] natures = desc.getNatureIds();
			
		String[] newNatures = new String[natures.length+1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = Activator.WYCLIPSE_NATURE_ID;
		desc.setNatureIds(newNatures);
		project.setDescription(desc, null);		
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IProject getProject() {
		return this.project;
	}
	
	/**
	 * Get the whileypath associated with this project. This is loaded from the
	 * <code>.whileypath</code> configuration file.
	 * 
	 * @return
	 */
	public WhileyPath getWhileyPath() {
		// TODO: actually read this from the whileypath file!!
				
		return null;
	}

	/**
	 * Set the project that this nature is associated with. This method gets
	 * called when the nature is first created, and therefore it needs to load
	 * the build configuration from the ".whileypath" file.
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;		
	}	
	
	/**
	 * Check whether verification is enabled or not. Enabling verification means
	 * the automated theorem prover will be used to check the
	 * pre-/post-conditions and other invariants of a Whiley module.
	 * 
	 * @return
	 */
	public boolean getVerificationEnable() throws CoreException {		
		String property = project.getPersistentProperty(VERIFICATION_PROPERTY);
		if(property == null) {
			return VERIFICATION_DEFAULT;
		} else {
			return property.equals("true");
		}
	}
	
	/**
	 * Set the verification enabled property. Enabling verification means the
	 * automated theorem prover will be used to check the pre-/post-conditions
	 * and other invariants of a Whiley module.
	 * 
	 * @return
	 */
	public void setVerificationEnable(boolean property) throws CoreException {
		project.setPersistentProperty(VERIFICATION_PROPERTY,
				Boolean.toString(property));
	}
}
