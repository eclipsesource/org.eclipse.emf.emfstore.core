/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.exceptions;

/**
 * Represents exceptional condition where the notification is unkown to a receiver.
 * 
 * @author koegel
 */
@SuppressWarnings("serial")
public class UnsupportedNotificationException extends Exception {

	/**
	 * Default constructor.
	 * 
	 * @param message the message
	 */
	public UnsupportedNotificationException(String message) {
		super(message);
	}

}
