package wyclipse.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import wybs.lang.Content;
import wybs.lang.Path;
import wybs.util.Trie;
import wyc.lang.WhileyFile;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wyil.lang.WyilFile;

/**
 * The <code>whileypath</code> controls the way in which files and folders in a
 * Whiley project are interpreted. In particular, some directories will be
 * identified as source folders, whilst others will be identifier as binary
 * output folders. Likewise, libraries (e.g. jar files) may be specified for
 * linking against.
 * 
 * @author David J. Pearce
 * 
 */
public final class WhileyPath {
	private final ArrayList<Entry> entries;
	private IPath defaultOutputFolder;
	
	public WhileyPath() {
		entries = new ArrayList<Entry>();
	}
	
	public WhileyPath(IPath defaultOutputFolder, Entry... entries) {
		this.defaultOutputFolder = defaultOutputFolder;
		this.entries = new ArrayList<Entry>();
		for(Entry e : entries) {
			this.entries.add(e);
		}
	}
	
	public WhileyPath(IPath defaultOutputFolder, Collection<Entry> entries) {
		this.defaultOutputFolder = defaultOutputFolder;
		this.entries = new ArrayList<Entry>(entries);
	}

	public IPath getDefaultOutputFolder() {
		return defaultOutputFolder;
	}
	
	public void setDefaultOutputFolder(IPath defaultOutputFolder) {
		this.defaultOutputFolder = defaultOutputFolder;
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
	
	public Document toXmlDocument() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			//root elements
			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("whileypath");
			doc.appendChild(root);
			root.appendChild(doc.createElement("src"));
			return doc;
		} catch(Exception e) {
			// ?
			return null;
		}
	}
	
	public static final WhileyPath fromXmlDocument(Document xmldoc) {
		System.out.println("FROM XML DOCUMENT CALLED");
		return new WhileyPath();
	}
	
	/**
	 * Represents an abstract item on the whileypath, which could be a build
	 * rule or a container of some sort.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Entry {
		
	}
	
	/**
	 * <p>
	 * Represents an external folder or library on the whilepath which contains
	 * various files needed for compilation. External files are not modified in
	 * any way by the builder.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> currently, external libraries must hold WyIL files.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ExternalLibrary extends Entry {
		/**
		 * The location of the folder containing whiley source files. Observe
		 * that this may be relative to the project root, or an absolute
		 * location.
		 */
		private IPath location;
		
		/**
		 * Describes the set of files which are included in this library.
		 */
		private Path.Filter includes;
		
		public ExternalLibrary(IPath location, Path.Filter includes) {	
			this.location = location;
			this.includes = includes;
		}
		
		public IPath getLocation() {
			return location;
		}
		
		public Path.Filter getIncludes() {
			return includes;
		}
	}
	
	/**
	 * Represents an action for compiling Whiley source files to a given target.
	 * An optional output folder may also be supplied. From this action, the
	 * necessary build rules for generating code for the given target can then
	 * be created.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Action extends Entry {
		
		/**
		 * The location of the folder containing whiley source files. Observe
		 * that this may be relative to the project root, or an absolute
		 * location.
		 */
		private IPath sourceFolder;
		
		/**
		 * Describes the set of source files which are included in this action.
		 */
		private Path.Filter sourceIncludes;
		
		/**
		 * The location of the folder where binary (i.e. compiled) files are
		 * placed. Observe that this may be relative to the project root, or an
		 * absolute location. Note also that this is optional, and may be null
		 * (in which case the defaultOutputFolder is used).
		 */
		private IPath outputFolder;
	
		public Action(IPath sourceFolder, Path.Filter sourceIncludes, IPath outputFolder) {
			this.sourceFolder = sourceFolder;
			this.sourceIncludes = sourceIncludes;
			this.outputFolder = outputFolder;
		}	
		
		
		public IPath getSourceFolder() {
			return sourceFolder;
		}
		
		public Path.Filter getSourceIncludes() {
			return sourceIncludes;
		}
		
		public IPath getOutputFolder() {
			return outputFolder;
		}
	}
}
