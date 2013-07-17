/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.server;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;

/**
 * Abstract URIConverter for normalizing EMFStore URIs on server side. Delegates normalizing to specialized methods
 * which have to be implemented by extenders.
 * 
 * @author jfaltermeier
 * 
 */
public abstract class AbstractESServerURIConverter extends ExtensibleURIConverterImpl {

	@Override
	public URI normalize(URI uri) {
		// emfstore:
		if (uri.scheme() != null && uri.scheme().equals(ServerURIUtil.SCHEME)) {

			// emfstore:/serverspaces/0
			if (uri.segment(0).equals(ServerURIUtil.SERVER_SEGMENT)) {

				// emfstore:/serverspaces/0/serverspace
				if (uri.segment(2).equals(ServerURIUtil.SERVERSPACE_SEGMENT)) {
					return normalizeServerSpaceURI(uri.segment(1));
				}

				// emfstore:/serverspaces/0/dynamic-models/model.ecore
				else if (uri.segment(2).equals(ServerURIUtil.DYNAMIC_MODELS_SEGMENT)) {
					return normalizeDynamicModelsURI(uri.segment(1), uri.segment(3));
				}

				// emfstore:/serverspaces/0/projects/<identifier>
				else if (uri.segment(2).equals(ServerURIUtil.PROJECTS_SEGMENT)) {
					return normalizeProjects(uri);
				}
			}
		}

		// unexpected
		return super.normalize(uri);
	}

	private URI normalizeProjects(URI uri) {
		// emfstore:/serverspaces/0/projects/<identifier>/projecthistory
		if (uri.segment(4).equals(ServerURIUtil.PROJECTHISTORY_SEGMENT)) {
			return normalizeProjectHistoryURI(uri.segment(1), uri.segment(3));
		}

		// emfstore:/serverspaces/0/projects/<identifier>/versions/<nr>
		else if (uri.segment(4).equals(ServerURIUtil.VERSIONS_SEGMENT)) {
			return normalizeVersionURI(uri.segment(1), uri.segment(3), Integer.valueOf(uri.segment(5)));
		}

		// emfstore:/serverspaces/0/projects/<identifier>/changepackages/<nr>
		else if (uri.segment(4).equals(ServerURIUtil.CHANGEPACKAGES_SEGMENT)) {
			return normalizeChangePackageURI(uri.segment(1), uri.segment(3), Integer.valueOf(uri.segment(5)));
		}

		// emfstore:/serverspaces/0/projects/<identifier>/projectstates/<nr>
		else if (uri.segment(4).equals(ServerURIUtil.PROJECTSTATES_SEGMENT)) {
			return normalizeProjectStateURI(uri.segment(1), uri.segment(3), Integer.valueOf(uri.segment(5)));
		}

		// unexpected
		else {
			return super.normalize(uri);
		}
	}

	/**
	 * Normalizes an EMFStore serverspace URI.
	 * 
	 * @param profile the selected profile
	 * @return the normalized URI
	 */
	protected abstract URI normalizeServerSpaceURI(String profile);

	/**
	 * Normalizes EMFStore dynamic model URI.
	 * 
	 * @param profile the selected profile
	 * @param ecoreName the file name of the dynamic model
	 * @return the normalized URI
	 */
	protected abstract URI normalizeDynamicModelsURI(String profile, String ecoreName);

	/**
	 * Normalizes an EMFStore projecthistory URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @return the normalized URI
	 */
	protected abstract URI normalizeProjectHistoryURI(String profile, String projectId);

	/**
	 * Normalizes an EMFStore version URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @param version the version
	 * @return the normalized URI
	 */
	protected abstract URI normalizeVersionURI(String profile, String projectId, int version);

	/**
	 * Normalizes an EMFStore changepackage URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @param version the version
	 * @return the normalized URI
	 */
	protected abstract URI normalizeChangePackageURI(String profile, String projectId, int version);

	/**
	 * Normalizes an EMFStore projectstate URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @param version the version
	 * @return the normalized URI
	 */
	protected abstract URI normalizeProjectStateURI(String profile, String projectId, int version);

}
