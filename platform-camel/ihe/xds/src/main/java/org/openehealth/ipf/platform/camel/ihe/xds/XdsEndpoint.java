/*
 * Copyright 2011 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xds;

import org.apache.cxf.interceptor.InterceptorProvider;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.JaxWsServiceFactory;
import org.openehealth.ipf.commons.ihe.xds.core.XdsClientFactory;
import org.openehealth.ipf.commons.ihe.xds.core.XdsServiceFactory;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsComponent;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiEndpoint;

/**
 * Camel Endpoint implementation for XDS-like transactions
 * which have only a single Web Service operation.
 * @author Dmytro Rud
 */
public class XdsEndpoint extends DefaultItiEndpoint<AbstractWsComponent<?>> {

    public XdsEndpoint(
            String endpointUri,
            String address,
            AbstractWsComponent<?> component,
            InterceptorProvider customInterceptors)
    {
        super(endpointUri, address, component, customInterceptors);
    }


    @Override
    public JaxWsClientFactory getJaxWsClientFactory() {
        return new XdsClientFactory(
                getComponent().getWsTransactionConfiguration(),
                getServiceUrl(),
                isAudit() ? getComponent().getClientAuditStrategy(isAllowIncompleteAudit()) : null,
                getCorrelator(),
                getCustomInterceptors());
    }


    @Override
    public JaxWsServiceFactory getJaxWsServiceFactory() {
        return new XdsServiceFactory(
                getComponent().getWsTransactionConfiguration(),
                getServiceAddress(),
                isAudit() ? getComponent().getServerAuditStrategy(isAllowIncompleteAudit()) : null,
                getCustomInterceptors(),
                getRejectionHandlingStrategy());
    }
}
