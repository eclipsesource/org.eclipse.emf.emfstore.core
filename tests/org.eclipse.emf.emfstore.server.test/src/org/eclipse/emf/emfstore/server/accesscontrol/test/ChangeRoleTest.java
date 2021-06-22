/*******************************************************************************
 * Copyright (c) 2011-2021 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesPackage;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ChangeRoleTest extends ProjectAdminTest {

	// BEGIN COMPLEX CODE
	// Checkstyle complains about public modifier..
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	// END COMPLEX CODE

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit // needed for share
		);
	}

	@Test
	public void changeSAPAtoWriter() throws ESException {
		// setup
		makeUserPA();
		getLocalProject().shareProject(getUsersession(), new NullProgressMonitor());
		makeUserSA();
		ACUser user = ServerUtil.getUser(getSuperUsersession(), getUser());
		assertTrue(ProjectAdminTest.hasServerAdminRole(user.getId()));
		assertTrue(ProjectAdminTest.hasProjectAdminRole(user, getProjectSpace().getProjectId()));
		assertEquals(2, user.getRoles().size());

		// act
		getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), user.getId(),
			RolesPackage.eINSTANCE.getWriterRole());

		// assert
		user = ServerUtil.getUser(getSuperUsersession(), getUser());
		assertFalse(ProjectAdminTest.hasServerAdminRole(user.getId()));
		assertFalse(ProjectAdminTest.hasProjectAdminRole(user, getProjectSpace().getProjectId()));
		assertTrue(ProjectAdminTest.hasWriterRole(user.getId()));
		assertEquals(1, user.getRoles().size());
	}

}
