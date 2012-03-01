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

package wyclipse.builder;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.launching.JREContainer;
import org.eclipse.jdt.launching.*;
import org.osgi.framework.Bundle;

import wycore.lang.*;
import wycore.lang.Path;
import wycore.util.*;
import wyil.Pipeline;
import wyil.Pipeline.Template;
import wyc.builder.WhileyBuilder;
import wyc.lang.WhileyFile;
import wyclipse.Activator;
import wyclipse.WhileyProject;
import wyil.io.WyilFileWriter;
import wyil.lang.WyilFile;

import wyil.Transform;
import wyil.transforms.*;
import wyjc.io.ClassFileLoader;

public class Builder extends IncrementalProjectBuilder {
	private static final boolean verbose = true;
	
			
	private Project project;
	private WhileyProject whileyProject;
	
	public Builder() {
		
	}

	protected void initialise() throws CoreException {		
		IProject iproject = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) iproject
				.getNature(JavaCore.NATURE_ID);

		IWorkspace workspace = iproject.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();		
		whileyProject = new WhileyProject(workspace,javaProject);
		
		if(verbose) {			
			// whileyProject.setLogger(new Logger.Default(System.err));
		}		
	}
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if(project == null) {
			initialise();
		}
		
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			whileyProject.buildAll();
		} else if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD
				|| kind == IncrementalProjectBuilder.AUTO_BUILD) {
			IResourceDelta delta = getDelta(getProject());			
			if (delta == null) {
				whileyProject.buildAll();
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		
		return null;
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		
		actionChangedResources(delta);

		// finally, give the whiley project a changed to recompile any whiley
		// files that are affected the by changes. 
		
		whileyProject.build();		
	}
	
	/**
	 * Clean all derived files from this project.
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		whileyProject.clean();
	}

	/**
	 * This simply recurses the delta and actions all changes to the whiley
	 * project. The whiley project will then decide whether or not those changes
	 * are actually relevant.
	 * 
	 * @param delta
	 * @return
	 */
	protected void actionChangedResources(IResourceDelta delta) {		
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {					
					IResource resource = delta.getResource();
					if (resource != null) {
						switch(delta.getKind()) {
							case IResourceDelta.ADDED:
								whileyProject.added(resource);
								break;
							case IResourceDelta.REMOVED:
								whileyProject.removed(resource);
								break;
							case IResourceDelta.CHANGED:
								whileyProject.changed(resource);
								break;
						}						
					}
					return true; // visit children as well.
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}		
	}
		
	/**
	 * Remove all markers on those resources to be compiled. It is assumed that
	 * those resources supplied are only whiley source files.
	 * 
	 * @param resources
	 * @throws CoreException
	 */
	protected void clearSourceFileMarkers(List<IFile> resources) throws CoreException {
		for (IResource resource : resources) {			
			resource.deleteMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);			
		}
	}	
	
	protected void highlightSyntaxError(IResource resource, SyntaxError err)
			throws CoreException {
		IMarker m = resource.createMarker("wyclipse.whileymarker");
		System.out.println("START: " + err.start());
		m.setAttribute(IMarker.CHAR_START, err.start());
		m.setAttribute(IMarker.CHAR_END, err.end() + 1);
		m.setAttribute(IMarker.MESSAGE, err.msg());
		m.setAttribute(IMarker.LOCATION, "Whiley File");
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);			
	}	
}
