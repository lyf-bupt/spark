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

package echo.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

import echo.EchoBinding;

/**
 * Implementation of the Echo binding model.
 */
public class EchoBindingImpl implements EchoBinding, PolicySetAttachPoint {

    private String name;
    private String uri;
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();
    private IntentAttachPointType bindingType = null;

    public IntentAttachPointType getType() {
        return bindingType;
    }

    public void setType(IntentAttachPointType type) {
        this.bindingType = type;
    }

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public boolean isUnresolved() {
        // The sample binding is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample binding is always resolved
    }

    public void setPolicySets(List<PolicySet> policySets) {
        this.policySets = policySets;

    }

    public void setRequiredIntents(List<Intent> intents) {
        this.requiredIntents = intents;

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public List<PolicySet> getApplicablePolicySets() {
        return this.applicablePolicySets;
    }

}
