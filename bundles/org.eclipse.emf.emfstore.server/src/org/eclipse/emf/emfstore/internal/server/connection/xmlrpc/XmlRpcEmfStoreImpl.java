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
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.Activator;
import org.eclipse.emf.emfstore.internal.server.EMFStore;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util.ShareProjectAdapter;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileNotOnServerException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileChunk;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.ClientVersionInfo;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * XML RPC connection interface for emfstore.
 *
 * @author wesendon
 */
public class XmlRpcEmfStoreImpl implements EMFStore {

	private EMFStore getEmfStore() {
		return XmlRpcConnectionHandler.getEmfStore();
	}

	private AccessControl getAccessControl() {
		return XmlRpcConnectionHandler.getAccessControl();
	}

	/**
	 * Log in the given credentials.
	 *
	 * @param username
	 *            the name of the user
	 * @param password
	 *            the password of the user to be logged in
	 * @param clientVersionInfo
	 *            client version information
	 * @return an {@link AuthenticationInformation} instance holding information about the logged in session
	 * @throws AccessControlException
	 *             in case login fails
	 */
	public AuthenticationInformation logIn(String username, String password, ClientVersionInfo clientVersionInfo)
		throws AccessControlException {
		ESAuthenticationInformation authInfo;
		try {
			authInfo = getAccessControl().getLoginService()
				.logIn(username, password, clientVersionInfo.toAPI());
			log(MessageFormat.format("[Client: {0}-{1}] Login of user ''{2}'' succeeded.", clientVersionInfo.getName(), //$NON-NLS-1$
				clientVersionInfo.getVersion(), username));
		} catch (final AccessControlException ex) {
			log(MessageFormat.format("[Client: {0}-{1}] Login of user ''{2}'' failed.", clientVersionInfo.getName(), //$NON-NLS-1$
				clientVersionInfo.getVersion(), username), ex);
			throw ex;
		}
		return org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAuthenticationInformationImpl.class
			.cast(authInfo).toInternalAPI();
	}

	/**
	 * Logout the session with the given ID.
	 *
	 * @param sessionId
	 *            the ID of the session to be logged out
	 *
	 * @throws AccessControlException
	 *             in case logout fails
	 */
	public void logout(SessionId sessionId) throws AccessControlException {
		try {
			final ACUser user = resolveUserBySessionId(sessionId.getId());
			getAccessControl().getLoginService().logout(sessionId.toAPI());
			log(user, "Logout succeeded.", null); //$NON-NLS-1$
		} catch (final AccessControlException ex) {
			log(MessageFormat.format("[Session ID: {0}] Logout failed.", sessionId.getId()), ex); //$NON-NLS-1$
			throw ex;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTag(SessionId sessionId, ProjectId projectId, PrimaryVersionSpec versionSpec, TagVersionSpec tag)
		throws ESException {
		getEmfStore().addTag(sessionId, projectId, versionSpec, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	public ProjectInfo createEmptyProject(SessionId sessionId, String name, String description, LogMessage logMessage)
		throws ESException {
		final ProjectInfo projectInfo = getEmfStore().createEmptyProject(sessionId, name, description, logMessage);
		log(sessionId, MessageFormat.format("Created empty project ''{0}''", name)); //$NON-NLS-1$
		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId.getId());
		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);
		ShareProjectAdapter.attachTo(session, projectInfo.getProjectId());
		return projectInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	public ProjectInfo createProject(SessionId sessionId, String name, String description, LogMessage logMessage,
		Project project) throws ESException {
		final ProjectInfo projectInfo = getEmfStore().createProject(sessionId, name, description, logMessage, project);
		log(sessionId, MessageFormat.format("Created project ''{0}'' with initial state (shared project).", name)); //$NON-NLS-1$
		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId.getId());
		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);
		ShareProjectAdapter.attachTo(session, projectInfo.getProjectId());
		return projectInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	public PrimaryVersionSpec createVersion(SessionId sessionId, ProjectId projectId,
		PrimaryVersionSpec baseVersionSpec, AbstractChangePackage changePackage, BranchVersionSpec targetBranch,
		PrimaryVersionSpec sourceVersion, LogMessage logMessage) throws ESException, InvalidVersionSpecException {
		return getEmfStore().createVersion(sessionId, projectId, baseVersionSpec, changePackage, targetBranch,
			sourceVersion, logMessage);
	}

	/**
	 * {@inheritDoc}
	 */

	public void deleteProject(SessionId sessionId, ProjectId projectId, boolean deleteFiles) throws ESException {
		getEmfStore().deleteProject(sessionId, projectId, deleteFiles);
		log(sessionId, MessageFormat.format("Deleted project with ID ''{0}''. Deleted files: {1}", projectId.getId(), //$NON-NLS-1$
			deleteFiles));
	}

	/**
	 * {@inheritDoc}
	 */
	public FileChunk downloadFileChunk(SessionId sessionId, ProjectId projectId,
		FileTransferInformation fileInformation)
		throws ESException {
		try {
			return getEmfStore().downloadFileChunk(sessionId, projectId, fileInformation);
		} catch (final FileNotOnServerException ex) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ProjectHistory exportProjectHistoryFromServer(SessionId sessionId, ProjectId projectId)
		throws ESException {
		return getEmfStore().exportProjectHistoryFromServer(sessionId, projectId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<AbstractChangePackage> getChanges(SessionId sessionId, ProjectId projectId, VersionSpec source,
		VersionSpec target) throws ESException {
		return getEmfStore().getChanges(sessionId, projectId, source, target);
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	public List<BranchInfo> getBranches(SessionId sessionId, ProjectId projectId) throws ESException {
		return getEmfStore().getBranches(sessionId, projectId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<HistoryInfo> getHistoryInfo(SessionId sessionId, ProjectId projectId, HistoryQuery<?> historyQuery)
		throws ESException {
		return getEmfStore().getHistoryInfo(sessionId, projectId, historyQuery);
	}

	/**
	 * {@inheritDoc}
	 */
	public Project getProject(SessionId sessionId, ProjectId projectId, VersionSpec versionSpec)
		throws ESException {
		final Project project = getEmfStore().getProject(sessionId, projectId, versionSpec);
		log(sessionId, MessageFormat.format("Get project with ID ''{0}''.", projectId.getId())); //$NON-NLS-1$
		return project;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ProjectInfo> getProjectList(SessionId sessionId) throws ESException {
		return getEmfStore().getProjectList(sessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	public ProjectId importProjectHistoryToServer(SessionId sessionId, ProjectHistory projectHistory)
		throws ESException {
		final ProjectId projectId = getEmfStore().importProjectHistoryToServer(sessionId, projectHistory);
		log(sessionId,
			MessageFormat.format("Imported project history resulting in project with ID ''{0}''.", projectId.getId())); //$NON-NLS-1$
		return projectId;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTag(SessionId sessionId, ProjectId projectId, PrimaryVersionSpec versionSpec, TagVersionSpec tag)
		throws ESException {
		getEmfStore().removeTag(sessionId, projectId, versionSpec, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	public ACUser resolveUser(SessionId sessionId, ACOrgUnitId id) throws ESException {
		return getEmfStore().resolveUser(sessionId, id);
	}

	/**
	 * {@inheritDoc}
	 */
	public PrimaryVersionSpec resolveVersionSpec(SessionId sessionId, ProjectId projectId, VersionSpec versionSpec)
		throws ESException {
		return getEmfStore().resolveVersionSpec(sessionId, projectId, versionSpec);
	}

	/**
	 * {@inheritDoc}
	 */
	public void transmitProperty(SessionId sessionId, OrgUnitProperty changedProperty, ACUser tmpUser,
		ProjectId projectId) throws ESException {
		getEmfStore().transmitProperty(sessionId, changedProperty, tmpUser, projectId);
	}

	/**
	 * {@inheritDoc}
	 */
	public FileTransferInformation uploadFileChunk(SessionId sessionId, ProjectId projectId, FileChunk fileChunk)
		throws ESException {
		return getEmfStore().uploadFileChunk(sessionId, projectId, fileChunk);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EMFStoreProperty> setEMFProperties(SessionId sessionId, List<EMFStoreProperty> properties,
		ProjectId projectId) throws ESException {
		if (properties != null && properties.size() > 0) {
			return getEmfStore().setEMFProperties(sessionId, properties, projectId);
		}

		return new ArrayList<EMFStoreProperty>();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EMFStoreProperty> getEMFProperties(SessionId sessionId, ProjectId projectId) throws ESException {
		return getEmfStore().getEMFProperties(sessionId, projectId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#registerEPackage(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.ecore.EPackage)
	 */
	public void registerEPackage(SessionId sessionId, EPackage pkg) throws ESException {
		getEmfStore().registerEPackage(sessionId, pkg);

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#getVersion(SessionId)
	 */
	public String getVersion(SessionId sessionId) throws ESException {
		return getEmfStore().getVersion(sessionId);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#uploadChangePackageFragment(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope)
	 */
	public String uploadChangePackageFragment(SessionId sessionId, ProjectId projectId, ChangePackageEnvelope envelope)
		throws ESException {
		return getEmfStore().uploadChangePackageFragment(sessionId, projectId, envelope);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#downloadChangePackageFragment(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId, java.lang.String, int)
	 */
	public ChangePackageEnvelope downloadChangePackageFragment(SessionId sessionId, ProjectId projectId, String proxyId,
		int fragmentIndex)
		throws ESException {
		return getEmfStore().downloadChangePackageFragment(sessionId, projectId, proxyId, fragmentIndex);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#deleteFile(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.FileIdentifier)
	 */
	public void deleteFile(SessionId sessionId, ProjectId projectId, FileIdentifier fileIdentifier) throws ESException {
		getEmfStore().deleteFile(sessionId, projectId, fileIdentifier);
		log(sessionId, MessageFormat.format("Deleted file with ID ''{0}'' from project with ID ''{1}''", //$NON-NLS-1$
			fileIdentifier.getIdentifier(), projectId.getId()));
	}

	private void log(String message) {
		Activator.getDefault().logInfo(message);
	}

	private void log(String message, Throwable exception) {
		Activator.getDefault().logInfo(message, exception);
	}

	private void log(SessionId sessionId, String message) throws AccessControlException {
		log(sessionId, message, null);
	}

	private void log(SessionId sessionId, String message, Throwable exception) throws AccessControlException {
		final ACUser user = resolveUserBySessionId(sessionId.getId());
		log(user, message, exception);
	}

	private void log(ACUser user, String message, Throwable exception) {
		final String logMessage = MessageFormat.format("[Username: {0} | User ID: {1}] {2}", //$NON-NLS-1$
			user.getName(), user.getIdentifier(), message);
		Activator.getDefault().logInfo(logMessage, exception);
	}

	private ACUser resolveUserBySessionId(String sessionId) throws AccessControlException {
		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId);
		final ESUser resolvedUser = getAccessControl().getSessions().resolveUser(resolvedSession);
		return (ACUser) ESUserImpl.class.cast(resolvedUser).toInternalAPI();
	}
}
