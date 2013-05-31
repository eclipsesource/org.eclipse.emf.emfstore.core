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
package org.eclipse.emf.emfstore.client.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

public class ServerCommunicationTest extends BaseLoggedInUserTest {

	@Override
	@After
	public void tearDown() throws Exception {
		deleteRemoteProjects(usersession);
		super.tearDown();
		// deleteLocalProjects();
	}

	@AfterClass
	public static void tearDownClass() {
		for (final ServerInfo serverInfo : ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI()
			.getServerInfos()) {
			final Usersession lastUsersession = serverInfo.getLastUsersession();
			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {
					if (lastUsersession != null) {
						lastUsersession.setServerInfo(null);
					}
					serverInfo.setLastUsersession(null);
					return null;
				}
			});
		}
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI().getServerInfos().clear();
				ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI().save();
				return null;
			}
		});
	}

	protected static void deleteRemoteProjects(ESUsersession usersession) throws IOException, FatalESException,
		ESException {
		for (ESRemoteProject project : ESWorkspaceProviderImpl.INSTANCE.getWorkspace().getServers().get(0)
			.getRemoteProjects(usersession)) {
			project.delete(usersession, new NullProgressMonitor());
		}
	}

	@Test
	public void testUsersession() {
		assertEquals(usersession, server.getLastUsersession());
	}

	@Test
	public void testLogin() {
		assertTrue(usersession.isLoggedIn());
	}

	@Test
	public void testLogout() {
		assertTrue(usersession.isLoggedIn());
		try {
			usersession.logout();
			assertFalse(usersession.isLoggedIn());
		} catch (ESException e) {
			log(e);
			fail(e.getMessage());
		}

	}

	@Test
	public void testCreateRemoteProject() {
		try {
			ESRemoteProject remoteProject = server.createRemoteProject(usersession, "MyProject",
				new NullProgressMonitor());
			assertNotNull(remoteProject);
			assertEquals("MyProject", remoteProject.getProjectName());
			List<ESRemoteProject> remoteProjects = server.getRemoteProjects();
			assertEquals(1, remoteProjects.size());
			// we expect a copy to be returned
			assertFalse(remoteProject.equals(remoteProjects.get(0)));
			assertEquals(remoteProject.getProjectName(), remoteProjects.get(0).getProjectName());
		} catch (ESException e) {
			log(e);
			fail(e.getMessage());
		}

	}

	@Test
	public void testCreateRemoteProjectWithoutUsersession() {
		try {
			ESRemoteProject remoteProject = server.createRemoteProject("MyProject",
				new NullProgressMonitor());
			assertNotNull(remoteProject);
			assertEquals("MyProject", remoteProject.getProjectName());
			List<? extends ESRemoteProject> remoteProjects = server.getRemoteProjects();
			assertEquals(1, remoteProjects.size());
			// we expect a copy to be returned
			assertFalse(remoteProject.equals(remoteProjects.get(0)));
			assertEquals(remoteProject.getProjectName(), remoteProjects.get(0).getProjectName());
		} catch (ESException e) {
			log(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteRemoteProject() {
		try {
			ESRemoteProject remoteProject = server.createRemoteProject(usersession, "MyProject",
				new NullProgressMonitor());
			assertEquals(1, server.getRemoteProjects().size());
			remoteProject.delete(new NullProgressMonitor());
			assertEquals(0, server.getRemoteProjects().size());
		} catch (ESException e) {
			log(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetRemoteProjectsFromServer() {
		try {
			ESRemoteProject project = server.createRemoteProject(usersession, "MyProject", new NullProgressMonitor());
			server.createRemoteProject(usersession, "MyProject2", new NullProgressMonitor());
			assertEquals(2, server.getRemoteProjects().size());
			server.getRemoteProjects().add(project);
			assertEquals(2, server.getRemoteProjects().size());
		} catch (ESException e) {
			log(e);
			fail(e.getMessage());
		}
	}
}
