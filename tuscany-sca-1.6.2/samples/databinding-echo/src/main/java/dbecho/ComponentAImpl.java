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

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 567526 $ $Date: 2007-08-20 03:35:32 +0100 (Mon, 20 Aug 2007) $
 */
public class ComponentAImpl implements Interface1 {

    private Interface1 componentBReference;
    
    @Constructor
    public ComponentAImpl(@Reference(name = "componentBReference", required = true)
    Interface1 componentBReference) {
        this.componentBReference = componentBReference;
    }

    public String call(String msg) {
        String request = msg + " [" + msg.getClass().getName() + "]";
        System.out.println("ComponentA --> Received message: " + request);
        Object ret = componentBReference.call(msg);
        String response = ret + " [" + ret.getClass().getName() + "]";
        System.out.println("ComponentA --> Returned message: " + response);
        return (String) ret;
    }

    public String call1(String msg) {
        String request = msg + " [" + msg.getClass().getName() + "]";
        System.out.println("ComponentA --> Received message: " + request);
        Object ret = componentBReference.call1(msg);
        String response = ret + " [" + ret.getClass().getName() + "]";
        System.out.println("ComponentA --> Returned message: " + response);
        return (String) ret;
    }

    @Property(name="prefix")
    public void setPrefix(String prefix) {
        System.out.println("[Property] prefix: " + prefix);
    }

    @Property(name="prefix1")
    public void setPrefix1(String prefix1) {
        System.out.println("[Property] prefix1: " + prefix1);
    }  
    
    /**
     * @param bar the bar to set
     */
    @Property(name="bar")
    public void setBar(String bar) {
        System.out.println("[Property] bar: " + bar);
    }

}
