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

package crud.client;


import org.apache.tuscany.sca.host.embedded.SCADomain;

import crud.CRUD;

/**
 * A sample client that shows how to create an SCA domain, get a service, and
 * invoke service methods of a CRUD component.
 * 
 * The CRUD component uses an <implementation.crud> implementation extension
 * from module implementation-crud-extension.
 *  
 * @version $Rev: 608227 $ $Date: 2008-01-02 21:01:11 +0000 (Wed, 02 Jan 2008) $
 */
public class CRUDClient {

    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("crud.composite");
        CRUD crudService = scaDomain.getService(CRUD.class, "CRUDServiceComponent");
        
        String id = crudService.create("ABC");
        
        Object result = crudService.retrieve(id);
        System.out.println("Result from create: " + result);

        crudService.update(id, "EFG");
        result = crudService.retrieve(id);
        System.out.println("Result from update: " + result);
        
        crudService.delete(id);
        result = crudService.retrieve(id);
        System.out.println("Result from delete: " + result);
        
        scaDomain.close();
    }

}
