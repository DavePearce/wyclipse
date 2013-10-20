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
	
	public WhileyPath() {
		entries = new ArrayList<Entry>();
	}
	
	public WhileyPath(Collection<Entry> entries) {
		this.entries = new ArrayList<Entry>(entries);
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
	 * Represents an item on the whileypath which (in an abstract sense) holds
	 * other items (e.g. source files). This includes folders, as well as
	 * libraries (e.g. jar files).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Container extends Entry {
		
		/**
		 * A unique identifier for this container. This enables the container to
		 * be referenced from a build rule.
		 */
		private String ID;
		
		/**
		 * The location of this "container". Observe that this may be relative
		 * to the project root, or an absolute location.
		 */
		private IPath location;
		
		/**
		 * Describes the set of files which are included in this
		 * container.
		 */
		private Path.Filter includes;
		
		/**
		 * Describes the kind of files which are included in this container.
		 */
		private Content.Type contentType;

		public Container(String ID, IPath location, Path.Filter includes,
				Content.Type contentType) {
			this.location = location;
			this.ID = ID;
			this.includes = includes;
			this.contentType = contentType;
		}
		
		public IPath getLocation() {
			return location;
		}
		
		public String getID() {
			return ID;
		}
		
		public Path.Filter getIncludes() {
			return includes;
		}
		
		public Content.Type getContentType() {
			return contentType;
		}
	}
	
	/**
	 * Represents a source folder on the whilepath which normally contains
	 * <code>.whiley</code> (although, in fact, this is not necessary the case).
	 * Source folders determine files which are to be recompiled when a build is
	 * initiated.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class SourceFolder extends Container {
		public SourceFolder(String ID, IPath location,
				Path.Filter includes, Content.Type contentType) {
			super(ID, location, includes, contentType);
		}
	}
	
	/**
	 * Represents a binary folder on the whilepath which normally contains
	 * binary files (e.g. <code>.wyil</code> files). Binary files are temporary
	 * and are removed when the project is "cleaned".
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class BinaryFolder extends Container {
		public BinaryFolder(String ID, IPath location,
				Path.Filter includes, Content.Type contentType) {
			super(ID, location, includes, contentType);
		}
	}
	
	/**
	 * Represents an external folder or library on the whilepath which contains
	 * various files needed for compilation. External files are not modified in
	 * any way by the builder.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class External extends Container {
		public External(String ID, IPath location, Path.Filter includes,
				Content.Type contentType) {
			super(ID, location, includes, contentType);
		}
	}
	
	/**
	 * Represents a build rule which is responsible for compiling files from a
	 * source folder and writing them to an output folder. A builder must also
	 * be specified which determines how the files will be compiled.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Rule extends Entry {
		
		/**
		 * The name of the builder to be used when compiling files according to
		 * this rule. For example, "wyc" is the standard name of the Whiley
		 * Compiler, which compiles ".whiley" files to ".wyil" files.
		 */
		private final String builderID;
		
		/**
		 * ID of source folder. Observe that this should be a designated source
		 * folder.
		 */
		private final String sourceFolderID;
		
		/**
		 * ID of output (binary) folder. Observe that this should be a
		 * designated binary folder.
		 */
		private final String binaryFolderID;
	
		public Rule(String builderID, String sourceFolderID, String binaryFolderID) {
			this.builderID = builderID;
			this.sourceFolderID = sourceFolderID;
			this.binaryFolderID = binaryFolderID;
		}
		
		public String getBuilderID() {
			return builderID;
		}
		
		public String getSourceFolderID() {
			return sourceFolderID;
		}
		
		public String getBinaryFolderID() {
			return binaryFolderID;
		}
	}
}
