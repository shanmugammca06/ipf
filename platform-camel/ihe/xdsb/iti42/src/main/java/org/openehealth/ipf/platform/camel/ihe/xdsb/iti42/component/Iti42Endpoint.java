/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.xdsb.iti42.component;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.openehealth.ipf.platform.camel.ihe.xdsb.commons.DefaultItiEndpoint;
import org.openehealth.ipf.platform.camel.ihe.xdsb.commons.ItiServiceInfo;
import org.openehealth.ipf.platform.camel.ihe.xdsb.iti42.service.Iti42PortType;

import javax.xml.namespace.QName;
import java.net.URISyntaxException;

/**
 * The Camel endpoint for the ITI-42 transaction.
 */
public class Iti42Endpoint extends DefaultItiEndpoint {
    private static final ItiServiceInfo<Iti42PortType> SERVICE_INFO = new ItiServiceInfo<Iti42PortType>(
            new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Service", "ihe"),
            Iti42PortType.class,
            new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Binding_Soap12", "ihe"),
            false,
            "wsdl/iti42.wsdl");

    /**
     * Constructs the endpoint.
     * @param endpointUri
     *          the endpoint URI.
     * @param address
     *          the endpoint address from the URI.
     * @param iti42Component
     *          the component creating this endpoint.
     * @throws URISyntaxException
     *          if the endpoint URI was not a valid URI.
     */
    public Iti42Endpoint(String endpointUri, String address, Iti42Component iti42Component) throws URISyntaxException {
        super(endpointUri, address, iti42Component);
    }

    public Producer<Exchange> createProducer() throws Exception {
        return new Iti42Producer(this, SERVICE_INFO);
    }

    public Consumer<Exchange> createConsumer(Processor processor) throws Exception {
        return new Iti42Consumer(this, processor, SERVICE_INFO);
    }
}