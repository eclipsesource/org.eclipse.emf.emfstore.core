/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util;

import org.apache.ws.commons.util.NamespaceContextImpl;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.parser.TypeParser;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileChunk;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.xml.sax.SAXException;

/**
 * Type Factory for XML RPC Transportation.
 *
 * @author wesendon
 */
public class EObjectTypeFactory extends TypeFactoryImpl {

	/**
	 * Default constructor.
	 *
	 * @param pController
	 *            XML RPC controller.
	 */
	public EObjectTypeFactory(XmlRpcController pController) {
		super(pController);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeParser getParser(XmlRpcStreamConfig pConfig, NamespaceContextImpl pContext, String pURI,
		String pLocalName) {
		if (EObjectSerializer.EOBJECT_TAG.equals(pLocalName)) {
			return new EObjectDeserializer();
		}
		if (FileTransferInformationSerializer.FTI_TAG.equals(pLocalName)) {
			return new FileTransferInformationParser();
		}
		final TypeParser parser = super.getParser(pConfig, pContext, pURI, pLocalName);
		if (parser instanceof org.apache.xmlrpc.parser.SerializableParser) {
			throw new IllegalArgumentException("A SerializableParser is not supported"); //$NON-NLS-1$
		}
		return parser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeSerializer getSerializer(XmlRpcStreamConfig pConfig, Object pObject) throws SAXException {
		if (pObject instanceof EObject) {
			return new EObjectSerializer();
		}
		if (pObject instanceof FileTransferInformation || pObject instanceof FileChunk) {
			return new FileTransferInformationSerializer();
		}
		return super.getSerializer(pConfig, pObject);
	}

}
