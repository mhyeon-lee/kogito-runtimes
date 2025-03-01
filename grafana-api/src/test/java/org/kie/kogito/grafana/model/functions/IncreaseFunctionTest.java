/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.grafana.model.functions;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IncreaseFunctionTest {

    @Test
    public void testSumByFunction() {
        // Arrange
        BaseExpression baseExpression = new BaseExpression("prefix", "suffix");
        GrafanaFunction sumFunction = new IncreaseFunction(baseExpression, "1m");

        List<Label> labels = Collections.singletonList(new Label("key", "value"));

        // Act
        String result = sumFunction.render("body", labels);

        // Assert
        assertThat(result).isEqualTo("increase(prefix_body_suffix{key=value}[1m])");
    }
}
