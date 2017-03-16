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
package composite;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;


@Service(Target.class)
@Scope("COMPOSITE")
public class TargetImpl implements Target {

    private ComponentContext ctx; 

    @Context 
    public void setContext(ComponentContext context) {
        ctx = context;
    }

    public void someMethod(String arg) {
        System.out.println("Target: " + arg);
        SourceCallback sourceCallback = (SourceCallback)ctx.getRequestContext().getCallbackReference().getService();
        sourceCallback.receiveResult(arg + " -> Target.someMethod");
    }
}
