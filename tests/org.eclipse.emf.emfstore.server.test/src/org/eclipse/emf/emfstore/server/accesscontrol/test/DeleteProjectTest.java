/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.mocks.ConnectionMock;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test whether a project admin can delete a project without deleting
 * its files on the server.
 * Also test that roles are correctly removed from affected users and groups when a project is deleted.
 *
 * @author emueller
 *
 */
public class DeleteProjectTest extends ProjectAdminTest {

	private static final String READER_USER = "JonReader"; //$NON-NLS-1$
	private static final String WRITER_USER = "JaneWriter"; //$NON-NLS-1$

	private static final String PA_GROUP = "PaGroup"; //$NON-NLS-1$

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit);
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	@After
	public void after() {
		try {
			ServerUtil.deleteGroup(getSuperUsersession(), getNewGroupName());
			ServerUtil.deleteGroup(getSuperUsersession(), getNewOtherGroupName());
			ServerUtil.deleteGroup(getSuperUsersession(), PA_GROUP);
			final ACUser readerUser = ServerUtil.getUser(getSuperUsersession(), READER_USER);
			if (readerUser != null) {
				getSuperAdminBroker().deleteUser(readerUser.getId());
			}
			final ACUser writerUser = ServerUtil.getUser(getSuperUsersession(), WRITER_USER);
			if (readerUser != null) {
				getSuperAdminBroker().deleteUser(writerUser.getId());
			}
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
		super.after();
	}

	@Override
	@Before
	public void before() {
		super.before();
	}

	@Test
	public void deleteProjectSA() throws ESException {
		makeUserSA();
		// TODO:
		getUsersession().logout();
		getUsersession().refresh();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getLocalProject().getRemoteProject().delete(new NullProgressMonitor());
		final List<ProjectInfo> projectList = ESWorkspaceProviderImpl.getInstance().getConnectionManager()
			.getProjectList(
				ESSessionIdImpl.class.cast(getUsersession().getSessionId()).toInternalAPI());
		// TODO: not transparent with mock server
		final ConnectionMock mock = (ConnectionMock) ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		assertTrue(mock.didDeleteFiles());
		assertEquals(0, projectList.size());
	}

	@Test(expected = AccessControlException.class)
	public void deleteProjectNotSA() throws ESException {
		ProjectUtil.share(getUsersession(), getLocalProject());
		getLocalProject().getRemoteProject().delete(new NullProgressMonitor());
	}

	@Test
	public void deleteProjectPA() throws ESException, IOException {
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		getLocalProject().getRemoteProject().delete(new NullProgressMonitor());

		final List<ProjectInfo> projectList = ESWorkspaceProviderImpl.getInstance().getConnectionManager()
			.getProjectList(
				ESSessionIdImpl.class.cast(getUsersession().getSessionId()).toInternalAPI());
		// TODO: not transparent with mock server
		final ConnectionMock mock = (ConnectionMock) ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), getUser());

		assertFalse(mock.didDeleteFiles());
		assertEquals(0, projectList.size());
		assertFalse(hasProjectAdminRole(user.getId()));
	}

	@Test(expected = AccessControlException.class)
	public void deleteProjectNotPA() throws ESException {
		ProjectUtil.share(getUsersession(), getLocalProject());
		getLocalProject().getRemoteProject().delete(new NullProgressMonitor());
	}

	@Test
	public void deleteProjectCleanAllRoles() throws ESException, IOException {
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectId projectId = getProjectSpace().getProjectId();

		final ACOrgUnitId readerUser = ServerUtil.createUser(getSuperUsersession(), READER_USER);
		final ACOrgUnitId writerUser = ServerUtil.createUser(getSuperUsersession(), WRITER_USER);
		final ACOrgUnitId readerGroup = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId writerGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		final ACOrgUnitId paGroup = ServerUtil.createGroup(getSuperUsersession(), PA_GROUP);

		getSuperAdminBroker().changeRole(projectId, readerUser, Roles.reader());
		getSuperAdminBroker().changeRole(projectId, writerUser, Roles.writer());

		getSuperAdminBroker().changeRole(projectId, readerGroup, Roles.reader());
		getSuperAdminBroker().changeRole(projectId, writerGroup, Roles.writer());
		getSuperAdminBroker().changeRole(projectId, paGroup, Roles.projectAdmin());

		getLocalProject().getRemoteProject().delete(new NullProgressMonitor());

		assertFalse(hasReaderRole(readerUser));
		assertFalse(hasWriterRole(writerUser));
		assertFalse(hasProjectAdminRole(ServerUtil.getUser(getSuperUsersession(), getUser()).getId()));

		assertFalse(hasReaderRole(readerGroup));
		assertFalse(hasWriterRole(writerGroup));
		assertFalse(hasProjectAdminRole(paGroup));
	}

	@Test
	public void deleteProjectServerAdminRoleNotRemoved() throws ESException, IOException {
		makeUserSA();
		final ACOrgUnitId saGroup = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		getSuperAdminBroker().assignRole(saGroup, Roles.serverAdmin());
		getProjectSpace().delete(new NullProgressMonitor());
		assertTrue(hasServerAdminRole(ServerUtil.getUser(getSuperUsersession(), getUser()).getId()));
		assertTrue(hasServerAdminRole(saGroup));
	}
}