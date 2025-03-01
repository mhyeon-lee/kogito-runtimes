/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

@ApplicationScoped
public final class KnativeWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String NAME = "knative";

    public static final String OPERATION_PROPERTY_NAME = "operation";

    public static final String PATH_PROPERTY_NAME = "path";

    private final KnativeServerlessWorkflowCustomFunction customFunction;

    @Inject
    public KnativeWorkItemHandler(KnativeServerlessWorkflowCustomFunction customFunction) {
        this.customFunction = customFunction;
    }

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> arguments) {
        Map<String, Object> metadata = workItem.getNodeInstance().getNode().getMetaData();

        return customFunction.execute(
                (String) metadata.get(OPERATION_PROPERTY_NAME),
                metadata.getOrDefault(PATH_PROPERTY_NAME, "/").toString(),
                arguments);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
