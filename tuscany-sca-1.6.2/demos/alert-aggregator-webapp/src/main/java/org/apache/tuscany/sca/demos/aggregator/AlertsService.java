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

package org.apache.tuscany.sca.demos.aggregator;

import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;
import org.osoa.sca.annotations.Remotable;

/**
 * Retrieve and manage alerts
 *
 * @version $Rev: 573144 $ $Date: 2007-09-06 04:55:24 +0100 (Thu, 06 Sep 2007) $
 */
@Remotable
public interface AlertsService {

    /**
     * Return a structure holding all of the new alerts that have been found
     * 
     * @return the structure containing alerts 
     */
    public AlertsType getAllNewAlerts(String id);
    
}
