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

package helloworld;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.bpel.example.helloworld.HelloPortType;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Tests the BPEL service
 * 
 * @version $Rev: 636807 $ $Date: 2008-03-13 17:32:45 +0000 (Thu, 13 Mar 2008) $
 */
public class BPELHelloWorldTestCase extends TestCase {

    private SCADomain scaDomain;
    HelloPortType bpelService = null;
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("helloworld.composite");
        bpelService = scaDomain.getService(HelloPortType.class, "BPELHelloWorldComponent");

    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }
    
    public void testInvoke() throws Exception {
        String response = bpelService.hello("Hello");
        assertEquals("Hello World", response);
    }
}
