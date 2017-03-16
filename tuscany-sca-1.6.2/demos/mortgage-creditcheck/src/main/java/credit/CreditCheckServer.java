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

package credit;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * @version $Rev: 548540 $ $Date: 2007-06-19 02:05:01 +0100 (Tue, 19 Jun 2007) $
 */
public class CreditCheckServer {
    public static void main(String[] args) throws Exception {

        System.out.println("Starting the CreditCheck Service...");

        SCADomain domain = SCADomain.newInstance("http://localhost", "/", "CreditCheck.composite");

        System.out.println("Press Enter to Exit...");
        System.in.read();

        domain.close();
        System.out.println("Bye");
    }
}
