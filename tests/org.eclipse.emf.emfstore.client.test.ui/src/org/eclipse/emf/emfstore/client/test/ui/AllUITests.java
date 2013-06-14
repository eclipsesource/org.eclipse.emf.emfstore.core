/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui;

import static org.junit.Assert.fail;

import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.test.config.TestSessionProvider;
import org.eclipse.emf.emfstore.client.test.ui.controllers.AllUIControllerTests;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.server.ESEMFStoreController;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all UI tests.
 * 
 * @author emueller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllUIControllerTests.class })
public class AllUITests {

	public static final int TIMEOUT = 60000;

	private static void startEMFStore() {
		ServerConfiguration.setTesting(true);
		ServerConfiguration.getProperties().setProperty(ServerConfiguration.XML_RPC_PORT, String.valueOf(8080));
		try {
			ESEMFStoreController.startEMFStore();
		} catch (FatalESException e) {
			fail(e.getMessage());
		}
		SWTBotPreferences.TIMEOUT = TIMEOUT;
	}

	@BeforeClass
	public static void beforeClass() {
		ESWorkspaceProvider.INSTANCE.setSessionProvider(new TestSessionProvider());
		startEMFStore();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stopEMFStore();
	}

	private static void stopEMFStore() {
		ESEMFStoreController.stopEMFStore();
		try {
			// give the server some time to unbind from it's ips. Not the nicest solution ...
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			fail();
		}
	}
}
