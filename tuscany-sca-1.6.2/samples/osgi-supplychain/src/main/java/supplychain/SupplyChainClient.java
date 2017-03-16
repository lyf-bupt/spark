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
package supplychain;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import supplychain.customer.Customer;


/**
 * This client program shows how to create an SCA runtime, start it,
 * locate a Customer service component and invoke it.
 */
public class SupplyChainClient {

    public static final void main(String[] args) throws Exception {
        SCADomain scaDomain = SCADomain.newInstance("supplychain.composite");
        Customer customer = scaDomain.getService(Customer.class, "CustomerComponent");

        System.out.println("Main thread " + Thread.currentThread());
        customer.purchaseGoods();
        System.out.println("Main thread sleeping ...");
        Thread.sleep(1000);

        scaDomain.close();
    }
}
