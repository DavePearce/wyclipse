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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import wycore.lang.Content;
import wycore.lang.Path.*;
import wycore.util.AbstractRoot;
import wycore.util.AbstractEntry;

/**
 * A Directory represents a directory on the file system. Using this, we can
 * list items on the path and see what is there.
 * 
 * @author djp
 * 
 */
public class IContainerRoot implements Root {
	private final Content.Registry contentTypes;
	private final IContainer dir;	
		
	/**
	 * Construct a directory root from a given directory and file filter.
	 * 
	 * @param file
	 *            --- location of directory on filesystem.
	 */
	public IContainerRoot(IContainer dir, Content.Registry contentTypes) {
		this.contentTypes = contentTypes;
		this.dir = dir;
	}
	
	public boolean contains(Entry<?> e) {
		if(e instanceof IEntry) {
			IEntry ie = (IEntry) e;
			return dir.exists(ie.file.getLocation());
		}
		return false;
	}
	
	public boolean exists(ID id, Content.Type<?> ct) throws Exception {
		return dir.exists(new Path(id.toString()));
	}
	
	public <T> IEntry<T> get(ID id, Content.Type<T> ct) throws Exception {
		return new IEntry(id, dir.getFile(new Path(id.toString())));
	}
	
	public <T> List<Entry<T>> get(Content.Filter<T> filter) throws Exception {
		return Collections.EMPTY_LIST;
	}
	
	public <T> Set<ID> match(Content.Filter<T> filter) throws Exception {
		return Collections.EMPTY_SET;
	}
		
	public <T> IEntry create(ID id, Content.Type<T> ct) throws Exception {
		return null; // to do!
	}
	
	public void flush() {
		
	}
	
	public void refresh() {
		
	}
	
	/*
	public List<Entry> list(PkgID pkg) throws CoreException {
		System.err.println("LISTING: " + pkg);
		Path path = new Path(pkg.toString().replace('.','/'));
		IResource member = dir.findMember(path);
		
		if (member.exists() && member instanceof IContainer) {
			IContainer container = (IContainer) member;
			ArrayList<Entry> entries = new ArrayList<Entry>();

			for (IResource file : container.members()) {				
				if(file instanceof IFile && file.getFileExtension().equals("class")) {
					String filename = file.getName();
					String name = filename.substring(0, filename.lastIndexOf('.'));
					ModuleID mid = new ModuleID(pkg, name);
					entries.add(new IEntry(mid, (IFile) file));				
				}
			}

			return entries;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	*/
	
	public String toString() {
		return dir.toString();
	}
	
	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class IEntry<T> extends AbstractEntry<T> {		
		private final IFile file;		
		
		public IEntry(ID mid, IFile file) {
			super(mid);			
			this.file = file;
		}
		
		public String location() {
			return file.toString();
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
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			contentType().write(out,contents);
			byte[] bytes = out.toByteArray();
			ByteArrayInputStream input = new ByteArrayInputStream(bytes);
			
			if(file.exists()) {
				// I don't really understand why I need to do this. I would have
				// expected the create method below to be sufficient.
				file.setContents(input, IResource.FORCE | IResource.DERIVED, null);
			} else {				
				file.create(input, IResource.FORCE | IResource.DERIVED, null);
			}
		}
		
		public InputStream inputStream() throws Exception {
			return file.getContents();		
		}
		
		public OutputStream outputStream() throws Exception {
			// BUMMER
			return null;		
		}
	}	
}