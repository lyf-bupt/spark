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

package calculator;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class CalculatorClient {
    public static void main(String[] args) throws Exception {

        SCANodeFactory factory = SCANodeFactory.newInstance();
        SCANode node = factory.createSCANodeFromClassLoader("Calculator.composite", CalculatorClient.class.getClassLoader());
        node.start();
        
        CalculatorService calculatorService = ((SCAClient)node).getService(CalculatorService.class, "CalculatorServiceComponent");
        
        // Calculate
        System.out.println("3 + 2=" + calculatorService.add(3, 2));
        System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
        System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
        System.out.println("3 / 2=" + calculatorService.divide(3, 2));

        node.stop();
    }

}
