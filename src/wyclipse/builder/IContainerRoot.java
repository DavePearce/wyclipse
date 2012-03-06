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
import java.util.*;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import wybs.lang.Content;
import wybs.lang.Path.*;
import wybs.util.AbstractRoot;
import wybs.util.AbstractEntry;
import wybs.util.Trie;

/**
 * An implementation of <code>Path.Root</code> which is backed by an Eclipse
 * <code>IContainer</code>.
 * 
 * @author David J. Pearce
 * 
 */
public class IContainerRoot extends AbstractRoot {	
	private final IContainer dir;		
		
	/**
	 * Construct a directory root from a given directory and file filter.
	 * 
	 * @param file
	 *            --- location of directory on filesystem.
	 */
	public IContainerRoot(IContainer dir, Content.Registry contentTypes) {
		super(contentTypes);		
		this.dir = dir;
	}

	public IContainer getContainer() {
		return dir;
	}
	
	public IFileEntry<?> getResource(IResource file) throws CoreException {		
		try {
			for(int i=0;i!=size();++i) {
				IFileEntry e = (IFileEntry) get(i);			
				if(e.file.equals(file)) {
					return e;
				}
			}
		} catch(CoreException e) {
			throw e;
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception other) {
			// hmmm, obviously I don't like doing this probably the best way
			// around it is to not extend abstract root.
		}
		return null;
	}
	
	/**
	 * Create an entry for the given resource (if appropriate). If the entry is
	 * of no interest to this root, then nothing is created and it returned
	 * null.
	 * 
	 * @param resource
	 * @return
	 * @throws CoreException
	 */
	public IFileEntry create(IResource resource) throws CoreException {
		IPath path = resource.getFullPath();
		try {
			if (dir.getFullPath().isPrefixOf(path)
					&& resource instanceof IFile) {
				IFile file = (IFile) resource;
				ID id = path2ID(path);			
				String suffix = file.getFileExtension();
				if (suffix != null
						&& (suffix.equals("class") || suffix.equals("whiley"))) {
					String filename = file.getName();
					String name = filename.substring(0, filename.lastIndexOf('.'));
					IFileEntry entry = new IFileEntry(id.append(name), (IFile) file);			
					contentTypes.associate(entry);
					insert(entry);
					return entry;
				}
			}
		} catch(CoreException e) {
			throw e;
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			// hmmm, obviously I don't like doing this probably the best way
			// around it is to not extend abstract root. 
		}
		return null;
	}
	
	public <T> Entry<T> create(ID id, Content.Type<T> contentType, Entry<?>... sources) throws Exception {
		Entry<T> entry = get(id,contentType);
		if(entry != null) {
			return entry;
		}
		Path path = new Path(id.toString() + "." + contentTypes.suffix(contentType)); 
		IFile file = dir.getFile(path);		
		entry = new IFileEntry<T>(id,file);
		insert(entry);
		entry.associate(contentType,null);		
		return entry;
	}
	
	public void flush() {
		
	}
	
	public void refresh() {
		contents = null;
	}
		
	public IFileEntry<?>[] contents() throws CoreException {
		// using instanceof is a bit of a hack here.
		if(contents instanceof IFileEntry<?>[]) {
			return (IFileEntry<?>[]) contents;
		} else {
			ArrayList<IFileEntry> contents = new ArrayList<IFileEntry>();			
			traverse(dir,Trie.ROOT,contents);
			IFileEntry<?>[] tmp = contents.toArray(new IFileEntry[contents.size()]);
			this.contents = tmp;
			this.nentries = contents.size();
			return tmp;
		}
	}
		
	public String toString() {
		return dir.toString();
	}
	
	public ID path2ID(IPath path) {		
		path = path.removeFileExtension();
		Trie id = Trie.ROOT;
		for(int i=0;i!=path.segmentCount();++i) {
			id = id.append(path.segment(i));
		}
		return id;
	}
	
	private void traverse(IContainer container, Trie id,
			ArrayList<IFileEntry> entries) throws CoreException {

		for (IResource file : container.members()) {			
			if(file instanceof IFile) {
				String suffix = file.getFileExtension();
				if (suffix != null
						&& (suffix.equals("class") || suffix.equals("whiley"))) {
					String filename = file.getName();
					String name = filename.substring(0, filename.lastIndexOf('.'));
					IFileEntry entry = new IFileEntry(id.append(name), (IFile) file);
					entries.add(entry);
					contentTypes.associate(entry);
				}
			} else if(file instanceof IFolder) {
				IFolder folder = (IFolder) file;
				traverse(folder,id.append(folder.getName()),entries);
			}
		}
	}
	
	/**
	 * An IFileEntry is a file on the file system which represents a Whiley
	 * module. The file may be encoded in a range of different formats. For
	 * example, it may be a source file and/or a binary wyil file.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class IFileEntry<T> extends AbstractEntry<T> {		
		private final IFile file;		
		
		public IFileEntry(ID mid, IFile file) {
			super(mid);			
			this.file = file;
		}
		
		public IFile getFile() {
			return file;
		}
		
		public String location() {
			return file.getLocation().toFile().toString();			
		}
		
		public long lastModified() {
			return file.getModificationStamp();
		}
		
		public String suffix() {
			String filename = file.getName();
			String suffix = "";
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				suffix = filename.substring(pos + 1);
			}
			return suffix;
		}
				
		public void write(T contents) throws Exception {
			super.write(contents);			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			contentType().write(out,contents);
			byte[] bytes = out.toByteArray();
			ByteArrayInputStream input = new ByteArrayInputStream(bytes);
			
			if(file.exists()) {
				// File already exists, so update contents.
				file.setContents(input, IResource.FORCE | IResource.DERIVED, null);
			} else {
				// first, ensure containing folder exists
				create(file.getParent());
				// finally, create file
				file.create(input, IResource.FORCE | IResource.DERIVED, null);
			} 
		}
		
		public void refresh() {
			if(!modified) {
				contents = null; // reset contents
			}
		}
		
		public InputStream inputStream() throws CoreException {
			return file.getContents();		
		}
		
		public OutputStream outputStream() throws CoreException {
			// BUMMER
			return null;		
		}
		
		/**
		 * The following method traverses the folder hierarchy from a given
		 * point and creates all IFolders that it encounters.  
		 * 
		 * @param container
		 * @throws CoreException
		 */
		private void create(IContainer container) throws CoreException {			
			if(container.exists()) {
				return;
			}
			IContainer parent = container.getParent();
			if(parent instanceof IFolder) {
				create((IFolder)parent);
			} 
			if(container instanceof IFolder) {
				IFolder folder = (IFolder) container;
				folder.create(IResource.FORCE | IResource.DERIVED, true, null);
			}
		}
	}	
}