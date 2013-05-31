/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.server;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.emfstore.client.test.Activator;
import org.eclipse.emf.emfstore.client.test.WorkspaceTest;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileTransferManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.junit.Test;

public class FileTransferTest extends WorkspaceTest {

	@Test
	public void testReinitWorkspace() throws FileTransferException, IOException {
		Configuration.getClientBehavior().setAutoSave(false);
		FileTransferManager transferManager = ((ProjectSpaceBase) getProjectSpace()).getFileTransferManager();
		transferManager.addFile(new File(FileLocator.toFileURL(Activator.getDefault().getBundle().getEntry("."))
			.getPath() + "TestProjects/REGISPeelSessions.zip"));
		// transferManager.dispose();
	}
}
