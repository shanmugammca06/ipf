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
package org.openehealth.ipf.tutorials.osgi.ihe.pixv3.iti44.route

import org.apache.camel.spring.SpringRouteBuilder
import org.openehealth.ipf.platform.camel.core.util.Exchanges

/**
 * @author Dmytro Rud
 */
class GroovyRouteBuilder extends SpringRouteBuilder {

    def pixFeedRequestTranslator
    def pixFeedAckTranslator

    @Override
    public void configure() throws Exception {
        from('pixv3-iti44:pixv3-iti44-service1')
            .onException(Exception.class)
                .maximumRedeliveries(0)
                .end()
            .validate().iti44Request()
            .translateHL7v3toHL7v2(pixFeedRequestTranslator)
            .to('pix-iti8://localhost:8882')
            .translateHL7v2toHL7v3(pixFeedAckTranslator)
            .validate().iti44Response()
            

        from('xds-iti44:xds-iti44-service1')
            .onException(Exception.class)
                .maximumRedeliveries(0)
                .end()
            .process { 
                Exchanges.resultMessage(it).body = '<response from="XDS Manager"/>'
            }


    }
}
