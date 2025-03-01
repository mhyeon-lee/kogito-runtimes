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
package org.kie.kogito.jobs.management;

import java.net.URI;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class RestJobsServiceTest<T extends RestJobsService> {

    public static final String CALLBACK_URL = "http://localhost:8080";
    public static final String JOB_SERVICE_URL = "http://localhost:8085";
    public static final String JOB_ID = "456";
    public static final String TIMER_ID = "123";
    public static final String PROCESS_ID = "PROCESS_ID";
    public static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    public static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    public static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    public static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    public static final ZonedDateTime EXPIRATION_TIME = ZonedDateTime.parse("2023-01-13T10:20:30.000001+01:00[Europe/Madrid]");

    protected T tested;

    @BeforeEach
    void setUp() {
        tested = createJobService(JOB_SERVICE_URL, CALLBACK_URL);
    }

    public abstract T createJobService(String jobServiceUrl, String callbackUrl);

    @Test
    void testGetCallbackEndpoint() {
        ProcessInstanceJobDescription description = ProcessInstanceJobDescription.builder()
                .timerId(TIMER_ID)
                .expirationTime(ExactExpirationTime.now())
                .processInstanceId(PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .build();
        String callbackEndpoint = tested.getCallbackEndpoint(description);
        assertThat(callbackEndpoint)
                .isEqualTo("%s/management/jobs/%s/instances/%s/timers/%s",
                        CALLBACK_URL,
                        PROCESS_ID,
                        PROCESS_INSTANCE_ID,
                        description.id());
    }

    @Test
    void testGetJobsServiceUri() {
        URI jobsServiceUri = tested.getJobsServiceUri();
        assertThat(jobsServiceUri).hasToString(JOB_SERVICE_URL + "/v2/jobs");
    }

    @Test
    void testScheduleProcessJob() {
        ProcessJobDescription processJobDescription = ProcessJobDescription.of(ExactExpirationTime.of(EXPIRATION_TIME),
                1,
                PROCESS_ID);
        assertThatThrownBy(() -> tested.scheduleProcessJob(processJobDescription))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    protected ProcessInstanceJobDescription buildProcessInstanceJobDescription() {
        return ProcessInstanceJobDescription.builder()
                .timerId(TIMER_ID)
                .expirationTime(ExactExpirationTime.of(EXPIRATION_TIME))
                .processInstanceId(PROCESS_INSTANCE_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .nodeInstanceId(NODE_INSTANCE_ID)
                .build();
    }

    protected void assertExpectedJob(Job job, String expectedJobId) {
        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(expectedJobId);
        assertThat(job.getId()).contains(TIMER_ID);
        assertThat(job.getRecipient())
                .isNotNull()
                .isInstanceOf(HttpRecipient.class);
        HttpRecipient<?> httpRecipient = (HttpRecipient<?>) job.getRecipient();
        assertThat(httpRecipient.getMethod()).isEqualTo("POST");
        assertThat(httpRecipient.getUrl()).isEqualTo("%s/management/jobs/%s/instances/%s/timers/%s",
                CALLBACK_URL,
                PROCESS_ID,
                PROCESS_INSTANCE_ID,
                expectedJobId);
        assertThat(httpRecipient.getHeaders())
                .hasSize(5)
                .containsEntry("processId", PROCESS_ID)
                .containsEntry("processInstanceId", PROCESS_INSTANCE_ID)
                .containsEntry("rootProcessId", ROOT_PROCESS_ID)
                .containsEntry("rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID)
                .containsEntry("nodeInstanceId", NODE_INSTANCE_ID);
        assertThat(httpRecipient.getPayload()).isNull();
        assertThat(job.getSchedule())
                .isNotNull()
                .isInstanceOf(TimerSchedule.class);
        TimerSchedule timerSchedule = (TimerSchedule) job.getSchedule();
        assertThat(timerSchedule.getStartTime()).isEqualTo(EXPIRATION_TIME.toOffsetDateTime());
    }
}
