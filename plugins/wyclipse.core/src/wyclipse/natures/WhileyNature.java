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

package wyclipse.natures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import wybs.lang.Content;
import wybs.lang.Logger;
import wybs.lang.Path;
import wybs.lang.Pipeline;
import wybs.util.StandardBuildRule;
import wyc.builder.WhileyBuilder;
import wyc.lang.WhileyFile;
import wyclipse.Activator;
import wyclipse.WhileyProject;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wyil.checks.*;
import wyil.io.WyilFilePrinter;
import wyil.lang.WyilFile;
import wyil.transforms.*;
import wyjvm.lang.ClassFile;

/**
 * Represents the fundamental building block of the Wyclipse plugin. Attaching a
 * Whiley Nature to a project gives it the ability to compile and execute Whiley
 * files. The Whiley nature reads its build configuration from the
 * ".whileypath", which operates in a similar fashion as for JDT's ".classpath".
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyNature implements IProjectNature {

	/**
	 * The following describe the persistent properties maintained for the
	 * Whiley Compiler. These are user configurable parameters which contol the
	 * compilation process. For example, verification can be enabled or disabled
	 * through the "Whiley Compiler" property page.
	 */
	public static final QualifiedName VERIFICATION_PROPERTY = new QualifiedName(
			"", "VERIFICATION");
	public static final boolean VERIFICATION_DEFAULT = true;

	private IProject project;
		
	private WhileyProject whileyProject;
		
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
	 * Get the WhileyProject associated with this nature.
	 * 
	 * @return
	 */
	public WhileyProject getWhileyProject() {
		return whileyProject;
	}
	
	/**
	 * Set the project that this nature is associated with. This method gets
	 * called when the nature is first created, and therefore it needs to load
	 * the build configuration from the ".whileypath" file.
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;
		try {
			this.whileyProject = createFromWhileyPath();
		} catch (CoreException e) {
			System.out.println("UNHANDLED CORE EXCEPTION");
		}
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

		// FIXME: at this point, we need to notify this.builder of the change.
	}
	
	
	/**
	 * Create a Whiley Project from the ".whileypath" file. Specifically, we
	 * create the project then parse the file adding all build rules as
	 * appropriate.
	 */
	private WhileyProject createFromWhileyPath() throws CoreException {
		WhileyProject project = new WhileyProject();

		// Configure builder
		Pipeline pipeline = new Pipeline(defaultPipeline);
		WhileyBuilder builder = new WhileyBuilder(whileyProject, pipeline);
		StandardBuildRule rule = new StandardBuildRule(builder);
		builder.setLogger(new Logger.Default(System.err));
		
		// FIXME: actually do something to the build rule!
		
		project.add(rule);
		
		return project;
	}
	
	// =====================================================================
	// Registry
	// =====================================================================
	
	/**
	 * The master project content type registry. This associates suffixes with
	 * the corresponding Object for decoding them. In essence, this determines
	 * what file kinds are known to the compiler.
	 */
	public static final Content.Registry registry = new Content.Registry() {

		public void associate(Path.Entry e) {
			if (e.suffix().equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if (e.suffix().equals("wyil")) {
				e.associate(WyilFile.ContentType, null);
			} else if (e.suffix().equals("wyal")) {
				e.associate(WyalFile.ContentType, null);
			} else if (e.suffix().equals("wycs")) {
				e.associate(WycsFile.ContentType, null);
			} else if (e.suffix().equals("class")) {
				e.associate(ClassFile.ContentType, null);				
			} 
		}

		public String suffix(Content.Type<?> t) {
			if (t == WhileyFile.ContentType) {
				return "whiley";
			} else if (t == WyilFile.ContentType) {
				return "wyil";
			} else if (t == WyalFile.ContentType) {
				return "wyal";
			} else if (t == WycsFile.ContentType) {
				return "wycs";
			} else if (t == ClassFile.ContentType) {
				return "class";
			} else {
				return "dat";
			}
		}
	};
	
	// =====================================================================
	// Default Pipeline (for whiley -> wyil)
	// =====================================================================

	public static final List<Pipeline.Template> defaultPipeline = Collections
			.unmodifiableList(new ArrayList<Pipeline.Template>() {
				{
//					add(new Pipeline.Template(WyilFilePrinter.class,
//							Collections.EMPTY_MAP));
					add(new Pipeline.Template(DefiniteAssignmentCheck.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(ModuleCheck.class, Collections.EMPTY_MAP));
					add(new Pipeline.Template(RuntimeAssertions.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(BackPropagation.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(LoopVariants.class, Collections.EMPTY_MAP));
					add(new Pipeline.Template(ConstantPropagation.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(CoercionCheck.class, Collections.EMPTY_MAP));
					add(new Pipeline.Template(DeadCodeElimination.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(LiveVariablesAnalysis.class,
							Collections.EMPTY_MAP));
//					add(new Pipeline.Template(WyilFilePrinter.class,
//							Collections.EMPTY_MAP));
				}
			});

	/**
	 * Register default transforms. This is necessary so they can be referred to
	 * from the command-line using abbreviated names, rather than their full
	 * names.
	 */
	static {
		Pipeline.register(BackPropagation.class);
		Pipeline.register(DefiniteAssignmentCheck.class);
		Pipeline.register(LoopVariants.class);
		Pipeline.register(ConstantPropagation.class);
		Pipeline.register(ModuleCheck.class);
		Pipeline.register(RuntimeAssertions.class);
		Pipeline.register(CoercionCheck.class);
		Pipeline.register(WyilFilePrinter.class);
		Pipeline.register(DeadCodeElimination.class);
		Pipeline.register(LiveVariablesAnalysis.class);
	}
}
