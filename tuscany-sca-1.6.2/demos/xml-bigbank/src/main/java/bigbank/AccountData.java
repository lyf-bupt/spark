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

import java.io.IOException;

import javax.xml.stream.XMLStreamReader;

/**
 * @version $Rev: 582985 $ $Date: 2007-10-08 22:44:24 +0100 (Mon, 08 Oct 2007) $
 */
public interface AccountData {
    /**
     * Get the StAX stream that represent the accounts data
     * @return The StAX stream containign the accounts
     * @throws IOException
     */
    XMLStreamReader getAccounts() throws IOException;
}
