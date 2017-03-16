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
package org.example.orderservice;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import javax.xml.ws.Holder;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Tests that the order server responds.
 */
public class OrderServiceTestCase {

    private SCADomain scaDomain;

    @Before
    public void startServer() throws Exception {
        scaDomain = SCADomain.newInstance("META-INF/sca-deployables/orderws.composite");
    }

    @Test
    public void testOrderReviewApproved() throws IOException {
        OrderService orderService =
            scaDomain.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);

        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust1234");
        order.setTotal( 50.0 );

        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        orderService.reviewOrder( holder );
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() == Status.APPROVED );
    }

    @Test
    public void testOrderReviewRejected() throws IOException {
        OrderService orderService =
            scaDomain.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);

        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust2345");
        order.setTotal( 50000.0 );

        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        orderService.reviewOrder( holder );
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() == Status.REJECTED );
    }

    @Test
    public void testOrderReviewRandom() throws IOException {
        OrderService orderService =
            scaDomain.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);

        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust3456");
        order.setTotal( 600.0 );
        
        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        orderService.reviewOrder( holder );
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() != Status.CREATED );
    }

    @After
    public void stopServer() throws Exception {
        if (scaDomain != null)
            scaDomain.close();
    }

}
