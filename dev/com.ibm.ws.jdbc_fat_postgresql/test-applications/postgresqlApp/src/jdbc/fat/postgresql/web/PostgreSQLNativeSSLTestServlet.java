/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
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
package jdbc.fat.postgresql.web;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;

import org.junit.Test;

import componenttest.app.FATServlet;

@SuppressWarnings("serial")
@WebServlet("/PostgreSQLNativeSSLTestServlet")
public class PostgreSQLNativeSSLTestServlet extends FATServlet {

    @Test
    public void testSSLNonValidating() throws Exception {
        DataSource ds = InitialContext.doLookup("jdbc/postgres/ssl/native");
        try (Connection con = ds.getConnection()) {
            con.createStatement().execute("INSERT INTO people(id,name) VALUES(1,'sampledata')");
        }
    }

}
