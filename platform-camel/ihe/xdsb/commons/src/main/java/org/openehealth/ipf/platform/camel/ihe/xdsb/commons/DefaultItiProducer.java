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
package org.openehealth.ipf.platform.camel.ihe.xdsb.commons;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import static org.apache.commons.lang.Validate.notNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.openehealth.ipf.platform.camel.ihe.xdsb.commons.cxf.MustUnderstandDecoratorInterceptor;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * Camel producer used to make calls to a webservice.
 *
 * @param <T>
 *          the type of the webservice.
 *
 * @author Jens Riemschneider
 */
public abstract class DefaultItiProducer<T> extends DefaultProducer<Exchange> {
    private static final Log log = LogFactory.getLog(DefaultItiProducer.class);

    private final ThreadLocal<T> client = new ThreadLocal<T>();
    private final ItiServiceInfo<T> serviceInfo;
    private final DefaultItiEndpoint endpoint;

    /**
     * Constructs the producer.
     * @param endpoint
     *          the endpoint that creates this producer.
     * @param serviceInfo
     *          the info describing the web-service.
     */
    public DefaultItiProducer(DefaultItiEndpoint endpoint, ItiServiceInfo<T> serviceInfo) {
        super(endpoint);
        notNull(serviceInfo, "serviceInfo");
        this.serviceInfo = serviceInfo;
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        log.debug("Calling webservice on '" + serviceInfo.getServiceName() + "' with " + exchange);
        callService(exchange);
    }

    /**
     * Sends the exchange to the webservice.
     * @param exchange
     *          the exchange that is send and changed with the result of the call.
     */
    protected abstract void callService(Exchange exchange);

    /**
     * Retrieves the client stub for the webservice.
     * <p>
     * This method caches the client stub instance and therefore requires thread synchronization.
     * @return the client stub.
     */
    protected synchronized T getClient() {
        if (client.get() == null) {
            URL wsdlURL = getClass().getClassLoader().getResource(serviceInfo.getWsdlLocation());

            Service service = Service.create(wsdlURL, serviceInfo.getServiceName());

            T port = service.getPort(serviceInfo.getServiceClass());
            BindingProvider bindingProvider = (BindingProvider) port;

            Map<String,Object> reqContext = bindingProvider.getRequestContext();
            reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint.getServiceUrl());
            reqContext.put("org.apache.cxf.ws.addressing.using", Boolean.TRUE);

            Binding binding = bindingProvider.getBinding();
            SOAPBinding soapBinding = (SOAPBinding)binding;
            soapBinding.setMTOMEnabled(serviceInfo.isMtom());

            Client client = ClientProxy.getClient(port);
            MustUnderstandDecoratorInterceptor interceptor = new MustUnderstandDecoratorInterceptor();
            interceptor.setHeaders(Arrays.asList("{http://www.w3.org/2005/08/addressing}Action"));
            interceptor.setHeaders(Arrays.asList("{http://www.w3.org/2005/08/addressing}ReplyTo"));
            client.getOutInterceptors().add(interceptor);

            this.client.set(port);

            log.debug("Created client adapter for: " + serviceInfo.getServiceName());
        }
        return client.get();
    }
}