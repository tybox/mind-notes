package mindnotes.linker;

import java.io.File;
import java.io.FilenameFilter;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;

/**
 * 
 * A simple linker that generates a cache manifest file based on emitted
 * compilation artifacts.
 * 
 * In the current implementation, the linker generates a cache.manifest file in
 * the war/mindnotes directory. It contains references to all permutation files
 * and to all static files.
 * 
 * Inspiration/reference: a talk on Google I/O 2010
 * "GWT Linkers target HTML5 WebWorkers & more"
 * http://www.youtube.com/watch?v=omBURP0MxcI
 * 
 * @author dominik
 * 
 */
@LinkerOrder(Order.POST)
public class CacheManifestLinker extends AbstractLinker {

	@Override
	public String getDescription() {
		return "Generates a cache.manifest template file for HTML5 Offline Application Cache.";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context,
			ArtifactSet artifacts) throws UnableToCompleteException {

		ArtifactSet as = new ArtifactSet(artifacts);

		// look in the war directory for files that should be included
		File dir = new File("war/");
		if (dir.isDirectory()) {

			EmittedArtifact manifest = emitManifest(logger, context, as, dir);
			as.add(manifest);

		}

		return as;
	}

	private EmittedArtifact emitManifest(TreeLogger logger,
			final LinkerContext context, ArtifactSet as, File dir)
			throws UnableToCompleteException {
		StringBuilder sb = new StringBuilder("CACHE MANIFEST\n");
		sb.append("NETWORK:\n");
		sb.append("network/*\n");
		sb.append("*\n");
		sb.append("CACHE:\n");
		sb.append("# static files\n");

		// only get:
		// 1. static entry html file
		File[] entryFiles = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String filename) {
				return filename.equalsIgnoreCase(context.getModuleName()
						+ ".html");
			}
		});
		if (entryFiles != null && entryFiles.length > 0) {
			sb.append("../" + entryFiles[0].getName() + "\n");
		}

		// 2. content of "static" folder
		File staticDir = new File(dir, "static");
		if (staticDir.exists() && staticDir.isDirectory()) {
			appendFiles(dir.getPath(), "..", staticDir.getName(), sb);
		}

		sb.append("# generated files\n");
		for (EmittedArtifact ea : as.find(EmittedArtifact.class)) {
			if (ea.isPrivate())
				continue;
			if (ea.getPartialPath().endsWith(".gwt.rpc"))
				continue; // ignore .gwt.rpc files; they are not served to the
			// client anyway
			sb.append(ea.getPartialPath() + "\n");

		}
		EmittedArtifact manifest = emitString(logger, sb.toString(),
				"cache.manifest");
		return manifest;
	}

	private void appendFiles(String parentPath, String appendPath, String dir,
			StringBuilder sb) {
		File dirfile = new File(parentPath, dir);
		if (!dirfile.isDirectory())
			return;
		for (File f : dirfile.listFiles()) {

			// ignore hidden files
			if (f.getName().startsWith("."))
				continue;

			String appendFile = appendPath + "/" + dir;

			if (f.isDirectory()) {
				// recursive call
				appendFiles(dirfile.getPath(), appendFile, f.getName(), sb);

			} else {
				// append a new file declaration in the manifest
				sb.append(appendFile + "/" + f.getName() + "\n");
			}

		}

	}
}
