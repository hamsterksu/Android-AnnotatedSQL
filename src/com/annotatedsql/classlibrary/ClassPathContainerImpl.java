package com.annotatedsql.classlibrary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

import com.annotatedsql.Activator;

public class ClassPathContainerImpl implements IClasspathContainer {

	public final static String ID = "com.annotatedsql.LIBRARY";
	
	private File pluginFolder;
	private IPath path;
	
	public ClassPathContainerImpl(IPath path, final File pluginFolder){
		this.pluginFolder = pluginFolder;
		this.path = path;
	}
	
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL api = bundle.getEntry("sqlannotation-annotations.jar");
		if(api != null){
			try {
				String file = FileLocator.toFileURL(api).toString();
				if(file.startsWith("file:")){
					file = file.substring(5);
				}
				entryList.add(JavaCore.newLibraryEntry(
							new Path(file), 
							null, 
							null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return entryList.toArray(new IClasspathEntry[entryList.size()]);
	}

	@Override
	public String getDescription() {
		return "AnnotatedSQL library";
	}

	@Override
	public int getKind() {
		return IClasspathEntry.CPE_CONTAINER;
	}

	@Override
	public IPath getPath() {
		return path;
		/*entryList.add(JavaCore.newLibraryEntry(
                                new Path(resource.getFullPath()
                                        .toOSString()), null,
                                new Path("/")));*/
		/*ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
		// fetch the names of all files that match our filter
		File[] libs = _dir.listFiles(_dirFilter);
		for (File lib : libs) {
			// strip off the file extension
			String ext = lib.getName().split("[.]")[1];
			// now see if this archive has an associated src jar
			File srcArc = new File(lib.getAbsolutePath().replace("." + ext,
					"-src." + ext));
			Path srcPath = null;
			// if the source archive exists then get the path to attach it
			if (srcArc.exists()) {
				srcPath = new Path(srcArc.getAbsolutePath());
			}
			// create a new CPE_LIBRARY type of cp entry with an attached source
			// archive if it exists
			entryList.add(JavaCore.newLibraryEntry(
					new Path(lib.getAbsolutePath()), srcPath, new Path("/")));
		}
		// convert the list to an array and return it
		IClasspathEntry[] entryArray = new IClasspathEntry[entryList.size()];
		return (IClasspathEntry[]) entryList.toArray(entryArray);
*/
		//return null;
	}

}
