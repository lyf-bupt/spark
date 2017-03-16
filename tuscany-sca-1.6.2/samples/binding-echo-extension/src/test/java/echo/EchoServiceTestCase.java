/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package echo;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import echo.server.EchoServer;

/**
 * @version $Rev: 784450 $ $Date: 2009-06-13 19:54:32 +0100 (Sat, 13 Jun 2009) $
 */
public class EchoServiceTestCase extends TestCase {

    private SCADomain scaDomain;

    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("EchoBinding.composite");
    }

    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testEchoBinding() throws Exception {
        Object result = EchoServer.getServer().call("http://example.com/server", new Object[] {"foo"});
        assertEquals("oof", result);
    }

}
