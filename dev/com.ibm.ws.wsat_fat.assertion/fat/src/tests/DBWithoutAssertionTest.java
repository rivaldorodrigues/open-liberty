/*******************************************************************************
 * Copyright (c) 2019, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.transaction.fat.util.FATUtils;
import com.ibm.ws.wsat.fat.util.DBTestBase;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

@RunWith(FATRunner.class)
public class DBWithoutAssertionTest extends DBTestBase {

	@Server("WSATDB_Client")
	public static LibertyServer client;

	@Server("WSATDB_Server1")
	public static LibertyServer server1;

	@Server("WSATDB_Server2")
	public static LibertyServer server2;

	@BeforeClass
	public static void beforeTests() throws Exception {

		// Test URL
		appName = "wsatAppWithoutAssertion";

		server1.setHttpDefaultPort(server1Port);
		server2.setHttpDefaultPort(server2Port);

		DBTestBase.initWSATTest(client);
		DBTestBase.initWSATTest(server1);
		DBTestBase.initWSATTest(server2);

		ShrinkHelper.defaultDropinApp(client, appName, "web."+appName+".client","web."+appName+".server","web."+appName+".servlet","web."+appName+".utils");
		ShrinkHelper.defaultDropinApp(server1, appName, "web."+appName+".client","web."+appName+".server","web."+appName+".servlet","web."+appName+".utils");
		ShrinkHelper.defaultDropinApp(server2, appName, "web."+appName+".client","web."+appName+".server","web."+appName+".servlet","web."+appName+".utils");

		CLient_URL = "http://" + client.getHostname() + ":"
				+ client.getHttpDefaultPort();
		Server1_URL = "http://" + server1.getHostname() + ":"
				+ server1.getHttpDefaultPort();
		Server2_URL = "http://" + server2.getHostname() + ":"
				+ server2.getHttpDefaultPort();
		
		FATUtils.startServers(client, server1, server2);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		FATUtils.stopServers(client, server1, server2);

		DBTestBase.cleanupWSATTest(client);
		DBTestBase.cleanupWSATTest(server1);
		DBTestBase.cleanupWSATTest(server2);
	}
	
	@Before
	public void saveServerConfigs() throws Exception {
		client.saveServerConfiguration();
		server1.saveServerConfiguration();
		server2.saveServerConfiguration();
	}
	
	@After
	public void restoreServerConfigs() throws Exception {
		client.restoreServerConfigurationAndWaitForApps();
		server1.restoreServerConfigurationAndWaitForApps();
		server2.restoreServerConfigurationAndWaitForApps();
	}

	@Test
	public void test3DBsWithoutWSDL1_AllCommitByProxy() {
		String testURL = "/" + appName + "/ClientServlet";
		String wsatURL = CLient_URL + testURL + "?" + server1Name
				+ "p=" + commit + ":" + basicURL + ":"
				+ server1Port + "&" + server2Name + "p="
				+ commit + ":" + basicURL + ":"
				+ server2Port;
		commonTest(appName, wsatURL, goodResult, "1");
	}

	@Test
	public void test3DBsWithoutWSDL2_ClientRollbackByProxy() {
		String testURL = "/" + appName + "/ClientServlet";
		String wsatURL = CLient_URL + testURL + "?" + server1Name
				+ "p=" + commit + ":" + basicURL + ":"
				+ server1Port + "&" + server2Name + "p="
				+ commit + ":" + basicURL + ":"
				+ server2Port + "&" + clientName + "="
				+ rollback;
		commonTest(appName, wsatURL, goodResult, "0");
	}
	
	@Test
	public void test3DBsWithoutWSDL3_Server2RollbackByProxy() {
		String testURL = "/" + appName + "/ClientServlet";
		String wsatURL = CLient_URL + testURL + "?" + server1Name
				+ "p=" + commit + ":" + basicURL + ":"
				+ server1Port + "&" + server2Name + "p="
				+ rollback + ":" + basicURL + ":"
				+ server2Port;
		commonTest(appName, wsatURL, "Throw exception for rollback from server side!", "0");
	}

	@Test
	public void test3DBsWithoutWSDL4_AllCommitByProxyWithoutUserTransaction() {
		String testURL = "/" + appName + "/ClientServlet";
		String wsatURL = CLient_URL + testURL + "?" + server1Name
				+ "p=" + commit + ":" + basicURL + ":"
				+ server1Port + "&" + server2Name + "p="
				+ commit + ":" + basicURL + ":"
				+ server2Port + "&withouttrans=true";
		commonTest(appName, wsatURL, goodResult, "1");
	}

	@Test
	public void test3DBsWithoutWSDL5_Server2RollbackByProxyWithoutUserTransaction() {
		String testURL = "/" + appName + "/ClientServlet";
		String wsatURL = CLient_URL + testURL + "?" + server1Name
				+ "p=" + commit + ":" + basicURL + ":"
				+ server1Port + "&" + server2Name + "p="
				+ rollback + ":" + basicURL + ":"
				+ server2Port + "&withouttrans=true";
		commonTest(appName, wsatURL, "Throw exception for rollback from server side!", "1", "1", "0");
	}
}