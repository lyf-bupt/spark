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
package dbecho;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 * @version $Rev: 538844 $ $Date: 2007-05-17 10:12:08 +0100 (Thu, 17 May 2007) $
 */
public class EchoDataBindingClient {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain  = SCADomain.newInstance("EchoDataBinding.composite");
        
        Interface1 componentA = scaDomain.getService(Interface1.class, "ComponentA");
        String response = componentA.call("<message><foo>123</foo></message>");
        String response1= componentA.call1("<message><foo>123</foo></message>");
        
        
        System.out.println("call  response = " + response );
        System.out.println("call1 response = " + response1 );
       
        scaDomain.close();

    }

}
