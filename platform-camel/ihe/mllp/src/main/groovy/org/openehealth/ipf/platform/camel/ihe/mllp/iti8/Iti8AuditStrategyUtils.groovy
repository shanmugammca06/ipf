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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti8

import org.apache.camel.Exchange
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter
import org.openehealth.ipf.platform.camel.ihe.mllp.core.AuditUtils

/**
 * Audit Strategy Groovy Utils
 * @author Dmytro Rud
 */
final class Iti8AuditStrategyUtils  {

    static void enrichAuditDatasetFromRequest(Iti8AuditDataset auditDataset, MessageAdapter msg, Exchange exchange) {
        def pidSegment
        if(msg.MSH[9][2].value == 'A40') {
            def group = msg.PIDPD1MRGPV1
            pidSegment = group.PID[3]
            auditDataset.oldPatientId = group.MRG[1].value ?: null
        } else {
            pidSegment = msg.PID[3]
        }
        auditDataset.patientId = AuditUtils.pidList(pidSegment)?.join(msg.MSH[2].value[1])
    }

}
