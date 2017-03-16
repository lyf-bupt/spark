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
package xquery.quote;

import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.SCATestCaseRunner;
import org.example.avail.AvailQuote;
import org.example.avail.AvailRequest;
import org.example.price.PriceQuote;
import org.example.price.PriceRequest;
import org.example.price.ShipAddress;
import org.example.quote.Quote;
import org.example.quote.QuoteResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commonj.sdo.DataObject;

/**
 * Integration test for the XQuery implementation type
 * @version $Rev: 830026 $ $Date: 2009-10-26 23:44:59 +0000 (Mon, 26 Oct 2009) $
 * This test covers the most important integration scenarios for the xquery
 * implementation type and its corresponding saxon data bindings:
 * 
 * 1. There is a central component for invoking the different
 *    scenarios: QuoteJoinLocalComponent
 * 2. It provides the following tests:
 *    - invoke XQuery component in the current assembly, by providing all needed
 *      information as input parameters
 *    - invoke XQuery component in external assembly, which is exposed as a web 
 *      service
 *    - invoke XQuery component in the current assembly, which retrieves the needed
 *      information from the component properties
 *    - invoke XQuery component in the current assembly, which retrieves the needed
 *      information from its references to other components:
 *         - one of the components is in the current assembly
 *         - the other component is in anther assembly and it is exposed (and accessed)
 *           as web service
 *    
 *  3. All of the XQuery components have reference to a component for calculation of the
 *    total price 
 *  4. SDO is used for data interchange
 */
public class XQueryQuoteClientTestCase {

    public static boolean SHOW_DEBUG_MSG = false;

    private SCADomain scaDomain;
    private SCATestCaseRunner server;

    private QuoteJoinLocal quoteJoinLocal;

    @Before
    public void startClient() throws Exception {
        try {
            scaDomain = SCADomain.newInstance("xqueryquotewsclient.composite");

            quoteJoinLocal = scaDomain.getService(QuoteJoinLocal.class, "QuoteJoinLocalComponent");

            server = new SCATestCaseRunner(XQueryQuoteTestServer.class);
            server.before();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testQuoteJoin() {
        AvailQuote availQuote = QuoteDataUtil.buildAvailQuoteData();
        PriceQuote priceQuote = QuoteDataUtil.buildPriceQuoteData();

        if (SHOW_DEBUG_MSG) {
            System.out.println("Input quote for the price list:");
            QuoteDataUtil.serializeToSystemOut((DataObject)priceQuote, "priceQuote");
            System.out.println();
            System.out.println("Input quote for the availability:");
            QuoteDataUtil.serializeToSystemOut((DataObject)availQuote, "availQuote");
            System.out.println();
        }

        Quote quote = quoteJoinLocal.joinPriceAndAvailQuotes(priceQuote, availQuote, 0.1f);
        if (SHOW_DEBUG_MSG) {
            System.out.println();
            System.out.println("Output quote from local join:");
            QuoteDataUtil.serializeToSystemOut((DataObject)quote, "quote");
            System.out.println();
        }
        XQueryQuoteClientTestCase.assertQuote(availQuote, priceQuote, quote, 0.1f);

        quote = quoteJoinLocal.joinPriceAndAvailQuotes(priceQuote, availQuote, 0.2f);
        if (SHOW_DEBUG_MSG) {
            System.out.println();
            System.out.println("Output quote from local join (second invokation):");
            QuoteDataUtil.serializeToSystemOut((DataObject)quote, "quote");
            System.out.println();
        }
        XQueryQuoteClientTestCase.assertQuote(availQuote, priceQuote, quote, 0.2f);

        quote = quoteJoinLocal.joinPriceAndAvailQuotesWs(priceQuote, availQuote, 0.1f);
        if (SHOW_DEBUG_MSG) {
            System.out.println();
            System.out.println("Output quote from web service join:");
            QuoteDataUtil.serializeToSystemOut((DataObject)quote, "quote");
            System.out.println();
        }
        XQueryQuoteClientTestCase.assertQuote(availQuote, priceQuote, quote, 0.1f);

        quote = quoteJoinLocal.joinPriceAndAvailQuotes();
        if (SHOW_DEBUG_MSG) {
            System.out.println();
            System.out.println("Output quote from properties join:");
            QuoteDataUtil.serializeToSystemOut((DataObject)quote, "quote");
            System.out.println();
        }
        XQueryQuoteClientTestCase.assertQuote(availQuote, priceQuote, quote, 0.1f);

        quote = quoteJoinLocal.joinPriceAndAvailQuotes(0.1f);
        if (SHOW_DEBUG_MSG) {
            System.out.println();
            System.out.println("Output quote from external references join:");
            QuoteDataUtil.serializeToSystemOut((DataObject)quote, "quote");
            System.out.println();
        }
        XQueryQuoteClientTestCase.assertQuote(availQuote, priceQuote, quote, 0.1f);
    }

    @After
    public void stopClient() throws Exception {
        server.after();
        scaDomain.close();
    }

    public static void assertQuote(AvailQuote availQuote, PriceQuote priceQuote, Quote quote, float taxRate) {
        QuoteCalculatorImpl quoteCalculatorImpl = new QuoteCalculatorImpl();
    
        TestCase.assertEquals(priceQuote.getCustomerName(), quote.getName());
        ShipAddress shipAddress = priceQuote.getShipAddress();
        TestCase.assertEquals(shipAddress.getStreet() + ","
            + shipAddress.getCity()
            + ","
            + shipAddress.getState().toUpperCase()
            + ","
            + shipAddress.getZip(), quote.getAddress());
        List availRequests = availQuote.getAvailRequest();
        List priceRequests = priceQuote.getPriceRequests().getPriceRequest();
        List quoteResponses = quote.getQuoteResponse();
        TestCase.assertEquals(availRequests.size(), priceRequests.size());
        TestCase.assertEquals(availRequests.size(), quoteResponses.size());
    
        for (int i = 0; i < availRequests.size(); i++) {
            AvailRequest availRequest = (AvailRequest)availRequests.get(i);
            PriceRequest priceRequest = (PriceRequest)priceRequests.get(i);
            QuoteResponse quoteResponse = (QuoteResponse)quoteResponses.get(i);
            TestCase.assertEquals(availRequest.getWidgetId(), quoteResponse.getWidgetId());
            TestCase.assertEquals(priceRequest.getPrice(), quoteResponse.getUnitPrice());
            TestCase.assertEquals(availRequest.getRequestedQuantity(), quoteResponse.getRequestedQuantity());
            TestCase.assertEquals(availRequest.isQuantityAvail(), quoteResponse.isFillOrder());
            if (availRequest.getShipDate() == null) {
                TestCase.assertNull(quoteResponse.getShipDate());
            } else {
                TestCase.assertEquals(availRequest.getShipDate(), quoteResponse.getShipDate());
            }
            TestCase.assertEquals(taxRate, quoteResponse.getTaxRate());
            TestCase.assertEquals(quoteCalculatorImpl.calculateTotalPrice(taxRate,
                                                                          availRequest.getRequestedQuantity(),
                                                                          priceRequest.getPrice(),
                                                                          availRequest.isQuantityAvail()),
                                                                          quoteResponse.getTotalCost());
        }
    }
}
