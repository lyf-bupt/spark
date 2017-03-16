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

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @version $Rev: 1001526 $ $Date: 2010-09-26 22:35:52 +0100 (Sun, 26 Sep 2010) $
 */
@Service(ExchangeRate.class)
public class ExchangeRateImpl {
    @Reference
    protected CurrencyExchange exchangeRate;

    private final DocumentBuilder builder;

    public ExchangeRateImpl() {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieve the live currency exchange rate from a live feed and extract the data for a given
     * currecy using XPath
     * @param currency The currency
     * @return The exchange rate
     */
    public double getExchangeRate(String currency) {
        try {
            System.out.println("Retrieving exchange rate...");

            // first try to get a live exchange rate quote from the RSS feed
            SyndFeed feed = null;
            try {
                feed = exchangeRate.getRates();

            // if the RSS feed isn't responding, continue with the demo using historical data 
            } catch (Exception e) {
                System.out.println("Exchange rate live quote not available, using historical data");
                return 0.74107;
            }

            // extract the exchange rate from the feed data
            SyndEntry entry = (SyndEntry)feed.getEntries().get(0);
            String rateTable = entry.getDescription().getValue();

            Document doc = builder.parse(new ByteArrayInputStream(rateTable.getBytes()));
            Node node = doc.getDocumentElement();
            XPath path = XPathFactory.newInstance().newXPath();
            XPathExpression exp = path.compile("/TABLE/TR[TD[1]='" + currency.toUpperCase() + "']/TD[2]");
            Node rateNode = (Node)exp.evaluate(node, XPathConstants.NODE);
            double rate = Double.valueOf(rateNode.getTextContent().trim());
            System.out.println("Exchange rate: USD 1.0=" + currency + " " + rate);
            return rate;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
