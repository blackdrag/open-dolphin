/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

package org.opendolphin.core.server

import org.opendolphin.core.BasePresentationModel
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import groovy.transform.CompileStatic
import org.opendolphin.core.comm.SwitchPresentationModelCommand

@CompileStatic
class ServerPresentationModel extends BasePresentationModel {

    private static final String AUTO_ID_SUFFIX = "-AUTO-SRV"

    public ServerModelStore modelStore

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    ServerPresentationModel(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore) {
        super(id ?: makeId(serverModelStore), attributes)
        if (id?.endsWith(AUTO_ID_SUFFIX)) {
            throw new IllegalArgumentException("presentation model with self-provided id '$id' may not end with suffix '$AUTO_ID_SUFFIX' since that is reserved.")
        }
        modelStore = serverModelStore
    }

    static String makeId(ServerModelStore serverModelStore) {
        def newId = serverModelStore.pmInstanceCount++
        return "$newId"+AUTO_ID_SUFFIX
    }

    @Override
    void syncWith(PresentationModel sourcePresentationModel) {
        super.syncWith(sourcePresentationModel) // this may already trigger some value changes and metadata changes
        modelStore.currentResponse << new SwitchPresentationModelCommand(sourcePmId: sourcePresentationModel.id, pmId: id)
    }

// override with server specific return values to avoid casting in client code

    ServerAttribute getAt(String propertyName) {
        return (ServerAttribute) super.getAt(propertyName)
    }

    ServerAttribute getAt(String propertyName, Tag tag) {
        return (ServerAttribute) super.getAt(propertyName, tag)
    }
}
