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

package echo.provider;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

import echo.EchoBinding;

/**
 * Implementation of the Echo binding provider.
 */
class EchoReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private EchoBinding binding;

    EchoReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, EchoBinding binding) {
        this.reference = reference;
        this.binding = binding;
    }

    public Invoker createInvoker(Operation operation) {
        if (binding instanceof PolicySetAttachPoint) {
            PolicySetAttachPoint policySetAttachPoint = (PolicySetAttachPoint)binding;
            if (!policySetAttachPoint.getPolicySets().isEmpty()) {
                return new EchoBindingPoliciedInvoker(policySetAttachPoint.getPolicySets());
            }
        }
        return new EchoBindingInvoker(binding.getURI());
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {
    }

    public void stop() {
    }

}
