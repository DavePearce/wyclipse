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
import wyil.io.WyilFileWriter;
import wyil.lang.WyilFile;

import wyil.Transform;
import wyil.transforms.*;
import wyjc.io.ClassFileLoader;

public class Builder extends IncrementalProjectBuilder {
	private static final boolean verbose = true;
	
			
	private Project project;
	private ArrayList<IContainerRoot> sourceRoots;

	public Builder() {
		
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if(project == null) {
			initialiseProject();
		}
		
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD
				|| kind == IncrementalProjectBuilder.AUTO_BUILD) {
			IResourceDelta delta = getDelta(getProject());			
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		
		return null;
	}

	protected void fullBuild(IProgressMonitor monitor) throws CoreException {
		// Force a complete reinitialisation of the compiler. This is necessary
		// in case things changed, such as the CLASSPATH, etc.		
		initialiseProject();
					
		ArrayList<IFile> sourceFiles = identifyAllCompileableResources(); 
		clearSourceFileMarkers(sourceFiles);
				
		compile(sourceFiles);
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		ArrayList<IResource> resources = identifyChangedResources(delta);
				
		System.out.println("BUILD DELTA (" + resources.size() + ")");
		for(IResource r : resources) {
			System.out.println("CHANGED: " + r.getLocation() + " : " + r.getName());
		}
		// First, check whether any important resources have changed (e.g.
		// classpath). If so, then we need to reinitialise the compiler
		// accordingly.
		for(IResource resource : resources) {
			if(isClassPath(resource)) {				
				fullBuild(monitor);
				return;
			}
		}
		
		ArrayList<IFile> sourceFiles = identifyCompileableResources(resources); 
		clearSourceFileMarkers(sourceFiles);
		
		compile(sourceFiles);
	}
	
	/**
	 * Clean all derived files from this project.
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);

		// =====================================================
		// first, delete everything in the default output folder
		// =====================================================
		
		IPath defaultOutputLocation = javaProject.getOutputLocation();
		IFolder defaultOutputContainer = workspaceRoot
				.getFolder(defaultOutputLocation);

		if (defaultOutputContainer != null) {
			ArrayList<IFile> files = new ArrayList<IFile>();
			addMatchingFiles(defaultOutputContainer, "class", files);
			for (IFile file : files) {
				file.delete(true, monitor);
			}
		}

		// ========================================================
		// second, delete everything in the specific output folders.
		// ========================================================

		// TODO

	}

	protected void initialisePaths(ArrayList<Path.Root> externalRoots,
			ArrayList<IContainerRoot> sourceRoots)
			throws CoreException {
		IProject project = (IProject) getProject();	
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
				
		
		if (javaProject != null) {
			initialisePaths(javaProject.getRawClasspath(), externalRoots,
					sourceRoots, workspaceRoot, javaProject);
		}
	}
	
	protected void initialiseProject() throws CoreException {		
		IProject iproject = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) iproject
				.getNature(JavaCore.NATURE_ID);

		IWorkspace workspace = iproject.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();		

		// =========================================================
		// Initialise whiley and source paths
		// =========================================================
		IContainer defaultOutputDirectory = workspaceRoot.getFolder(javaProject.getOutputLocation());
		IContainerRoot outputRoot = defaultOutputDirectory != null ? new IContainerRoot(defaultOutputDirectory,registry) : null;
		
		ArrayList<Path.Root> externalRoots = new ArrayList();
		sourceRoots = new ArrayList<IContainerRoot>();
		if(outputRoot != null) {
			externalRoots.add(outputRoot);
		}
		initialisePaths(externalRoots,sourceRoots);		
		
		// =========================================================
		// Construct Namespace
		// =========================================================
		
		NameSpace namespace = new StandardNameSpace((ArrayList) sourceRoots, externalRoots) {        		
			public Path.ID create(String s) {
				return Trie.fromString(s);
			}
		};

		// =========================================================
		// Construct and Configure Project
		// =========================================================

		project = new Project(namespace);			

		if(verbose) {			
			project.setLogger(new Logger.Default(System.err));
		}		

		// now, initialise builder appropriately
		Pipeline pipeline = new Pipeline(Pipeline.defaultPipeline);
		WhileyBuilder builder = new WhileyBuilder(project,pipeline);
		Content.Filter<WhileyFile> includes = Content.filter(Trie.fromString("**"),WhileyFile.ContentType);
		StandardBuildRule rule = new StandardBuildRule(builder);
		
		for(Path.Root source : sourceRoots) {	
			if(outputRoot != null) {
				rule.add(source, includes, outputRoot, WyilFile.ContentType);
			} else {
				// default backup
				rule.add(source, includes, source, WyilFile.ContentType);
			}
		}
		
		project.add(rule);
		
	}

	protected void compile(List<IFile> compileableResources)
			throws CoreException {		
		
		HashMap<String, IFile> resourceMap = new HashMap<String, IFile>();
		try {
			ArrayList<Path.Entry<?>> files = new ArrayList();
			for (IFile resource : compileableResources) {
				for(IContainerRoot root : sourceRoots) {
					IContainerRoot.IFileEntry<?> e = root.get(resource);
					if(e != null) {
						// Refresh the entry since it's changed.
						e.refresh();
						
						files.add(e);						
						// FIXME: following is broken and needs to be fixed.
						File file = resource.getLocation().toFile();
						resourceMap.put(file.getAbsolutePath(), resource);						
					}
				}
			}
									
			project.build(files);

		} catch (SyntaxError e) {					
			IFile resource = resourceMap.get(e.filename());
			if(resource != null) { // saftey
				highlightSyntaxError(resource, e);
			} else {
				System.out.println("SKIPPING ERROR");
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	/**
	 * This simply recurses the delta and strips out the resources which have
	 * changed.
	 * 
	 * @param delta
	 * @return
	 */
	protected ArrayList<IResource> identifyChangedResources(IResourceDelta delta) {
		final ArrayList<IResource> files = new ArrayList<IResource>();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {					
					IResource resource = delta.getResource();
					if (resource != null) {
						files.add(resource);
					}
					return true; // visit children as well.
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return files;
	}

	/**
	 * Identify those resources which have changed, and which are allowed to be
	 * compiled. Resources which cannot be compiled include those which are not
	 * source files, or are not located in a designated source folder.
	 * 
	 * @param resources
	 * @return
	 */
	protected ArrayList<IFile> identifyCompileableResources(
			List<IResource> resources) throws CoreException {
		
		// First, identify source folders		
		
		IProject project = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);		
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		// FIXME: the following information about source folders could be
		// extracted from the compiler?
		
		ArrayList<IPath> sourceFolders = new ArrayList<IPath>(); 
				
		if (javaProject != null) {			
			for (IClasspathEntry e : javaProject.getRawClasspath()) {
				switch (e.getEntryKind()) {
					case IClasspathEntry.CPE_SOURCE :			
						IFolder folder = workspaceRoot.getFolder(e.getPath());						
						sourceFolders.add(folder.getLocation());
						break;					
				}
			}
		}
		
		ArrayList<IFile> files = new ArrayList<IFile>();
		for (IResource resource : resources) {
			if (resource.getType() == IResource.FILE
					&& resource.getFileExtension().equals("whiley")
					&& containedInFolders(resource.getLocation(), sourceFolders)) {
				files.add((IFile) resource);
			}
		}
		return files;
	}

	/**
	 * Identify those resources which have changed, and which are allowed to be
	 * compiled. Resources which cannot be compiled include those which are not
	 * source files, or are not located in a designated source folder.
	 * 
	 * @param resources
	 * @return
	 */
	protected ArrayList<IFile> identifyAllCompileableResources()
			throws CoreException {
		
		// First, identify source folders		
		
		IProject project = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);		
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		ArrayList<IContainer> sourceFolders = new ArrayList<IContainer>(); 
				
		if (javaProject != null) {			
			for (IClasspathEntry e : javaProject.getRawClasspath()) {
				switch (e.getEntryKind()) {
					case IClasspathEntry.CPE_SOURCE :			
						IFolder folder = workspaceRoot.getFolder(e.getPath());						
						sourceFolders.add(folder);
						break;					
				}
			}
		}
		
		ArrayList<IFile> files = new ArrayList<IFile>();
		for(IContainer root : sourceFolders) {		
			addMatchingFiles(root,"whiley",files);							
		}
		
		return files;
	}
	
	protected boolean containedInFolders(IPath path,
			ArrayList<IPath> folders) {
		for(IPath folder : folders) {			
			if(folder.isPrefixOf(path)) {
				return true;
			}
		}
		return false;
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
	
	private static boolean isClassPath(IResource resource) {
		return resource instanceof IFile && resource.getName().equals(".classpath");
	}
	
	private static void addMatchingFiles(IContainer resource,
			final String extension, final ArrayList<IFile> files) throws CoreException {
		IResourceVisitor visitor = new IResourceVisitor() {
			public boolean visit(IResource resource) {
				String suffix = resource.getFileExtension();
				if (resource.getType() == IResource.FILE
						&& suffix != null && suffix.equals(extension)) {
					files.add((IFile) resource);
				}
				return true; // visit children as well.
			}
		};
		resource.accept(visitor);
	}
}
