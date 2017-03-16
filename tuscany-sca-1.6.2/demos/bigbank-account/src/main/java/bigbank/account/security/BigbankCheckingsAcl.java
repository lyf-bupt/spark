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

package bigbank.account.security;

import java.security.Principal;
import java.util.Hashtable;
import java.util.Map;

/**
 * @version $Rev: 635619 $ $Date: 2008-03-10 17:54:29 +0000 (Mon, 10 Mar 2008) $
 */
public class BigbankCheckingsAcl {
    private static Map<String, String>checkingsAcl = new Hashtable<String, String>();
    
    static {
        checkingsAcl.put("bbaservice", "Customer_01");
        checkingsAcl.put("bbUser01", "Customer_01");
    }
    
    
    public static void authorize(Principal principal, String resource) {
        if ( checkingsAcl.get(principal.getName()) == null ||
            !checkingsAcl.get(principal.getName()).equals(resource) ) {
            throw new RuntimeException("User - " + principal.getName() + " not authorized to access account " +
                                       resource);
        } else {
            System.out.println("Successfully Authorized '" + principal.getName() + " to access accounts of " + resource);
        }
    }

}
