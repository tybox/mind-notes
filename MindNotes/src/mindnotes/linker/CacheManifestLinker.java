package mindnotes.linker;

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
 * In the current implementation, the linker generates a cache.manifest.template
 * file; This file does not include static files that might be needed to be
 * included in the manifest. You have to update the /cache.manifest file
 * manually.
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

		StringBuilder sb = new StringBuilder("CACHE MANIFEST\n");

		for (EmittedArtifact ea : as.find(EmittedArtifact.class)) {
			if (ea.isPrivate())
				continue;
			if (ea.getPartialPath().endsWith(".gwt.rpc"))
				continue; // ignore .gwt.rpc files; they are not served to the
							// client anyway
			// XXX or are they?
			sb.append(context.getModuleName() + "/");
			sb.append(ea.getPartialPath() + "\n");

		}
		EmittedArtifact manifest = emitString(logger, sb.toString(),
				"cache.manifest.template");
		as.add(manifest);

		return as;
	}
}
