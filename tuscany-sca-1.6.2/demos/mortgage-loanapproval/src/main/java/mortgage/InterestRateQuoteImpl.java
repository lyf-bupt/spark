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

@Service(InterestRateQuote.class)
public class InterestRateQuoteImpl implements InterestRateQuote {
    public float getRate(String state, double loanAmount, int termInYears) {
        float rate = 6.0f;
        if (termInYears == 5) {
            rate = 5.5f;
        } else {
            rate = 6.5f;
        }
        System.out.println("Interest rate for a " + termInYears
            + "-year loan of $"
            + loanAmount
            + " in "
            + state
            + ": "
            + rate
            + "%");
        return rate;
    }
}
