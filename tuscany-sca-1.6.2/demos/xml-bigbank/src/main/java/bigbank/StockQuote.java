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

package bigbank;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.osoa.sca.annotations.Remotable;

/**
 * @version $Rev: 647669 $ $Date: 2008-04-14 07:11:55 +0100 (Mon, 14 Apr 2008) $
 */
@Remotable
public interface StockQuote {
    /**
     * Invoke the stock quote web service to get the live quotes
     * @param input The StAX stream of the request
     * @return The StAX stream of the response
     */
    public OMElement GetQuote(XMLStreamReader input);
}
