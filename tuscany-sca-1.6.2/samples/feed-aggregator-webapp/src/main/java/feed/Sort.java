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
package feed;

import java.util.List;

import org.apache.abdera.model.Entry;

/**
 * The Sort service business interface.
 * 
 * @version $Rev: 639931 $ $Date: 2008-03-22 01:13:05 +0000 (Sat, 22 Mar 2008) $
 */
public interface Sort {

    /**
     * Sort feed entries by published date.
     * @param entries
     * @return
     */
    List<Entry> sort(List<Entry> entries);
}
