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
package mortgage;

import org.osoa.sca.annotations.Service;

/**
 * An implementation of the Calculator service.
 */
@Service(MortgageCalculator.class)
public class MortgageCalculatorImpl implements MortgageCalculator {

    public double getMonthlyPayment(double principal, int years, float interestRate) {
        double monthlyRate = interestRate / 12.0 / 100.0;
        double p = Math.pow(1 + monthlyRate, years * 12);
        double q = p / (p - 1);
        double monthlyPayment = principal * monthlyRate * q;
        System.out.println("Monthly payment for a " + years + "-year loan of $" + principal + ": " + monthlyPayment);
        return monthlyPayment;
    }

}
