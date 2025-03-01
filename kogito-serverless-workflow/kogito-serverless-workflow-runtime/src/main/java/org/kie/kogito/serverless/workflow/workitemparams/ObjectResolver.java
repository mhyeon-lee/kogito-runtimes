/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.workitemparams;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonObjectUtils;

public class ObjectResolver extends ExpressionWorkItemResolver<Object> {

    public ObjectResolver(String exprLang, Object expr, String paramName) {
        super(exprLang, expr, paramName);
    }

    @Override
    public Object apply(KogitoWorkItem workItem) {
        return JsonObjectUtils.simpleToJavaValue(evalExpression(workItem));
    }
}
