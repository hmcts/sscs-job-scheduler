package uk.gov.hmcts.reform.sscs.services.quartz;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import uk.gov.hmcts.reform.sscs.jobscheduler.model.Job;
import uk.gov.hmcts.reform.sscs.jobscheduler.model.JobDataKeys;
import uk.gov.hmcts.reform.sscs.jobscheduler.services.JobException;
import uk.gov.hmcts.reform.sscs.jobscheduler.services.quartz.JobClassMapper;
import uk.gov.hmcts.reform.sscs.jobscheduler.services.quartz.JobClassMapping;
import uk.gov.hmcts.reform.sscs.jobscheduler.services.quartz.QuartzJobScheduler;

@SuppressWarnings("unchecked")
public class QuartzJobSchedulerTest {

    private final Scheduler scheduler = mock(Scheduler.class);
    private final JobClassMapper jobClassMapper = mock(JobClassMapper.class);
    private final QuartzJobScheduler quartzJobScheduler = new QuartzJobScheduler(
        scheduler, jobClassMapper
    );
    private final JobClassMapping jobClassMapping = mock(JobClassMapping.class);

    @Test
    public void job_is_scheduled() {

        assertThatCode(
            () -> {

                String jobGroup = "test-job-group";
                String jobName = "test-job-name";
                String jobPayload = "payload";
                ZonedDateTime triggerAt = ZonedDateTime.now();

                Job<String> job = new Job<>(
                    jobGroup,
                    jobName,
                    jobPayload,
                    triggerAt
                );

                when(jobClassMapper.getJobMapping(String.class)).thenReturn(jobClassMapping);
                when(jobClassMapping.serialize(jobPayload)).thenReturn("serialized-payload");

                String actualJobId = quartzJobScheduler.schedule(job);

                ArgumentCaptor<JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(JobDetail.class);
                ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

                verify(scheduler, times(1)).scheduleJob(
                    jobDetailCaptor.capture(),
                    triggerCaptor.capture()
                );

                JobDetail actualJobDetail = jobDetailCaptor.getValue();
                assertThat(actualJobId).isEqualTo(actualJobDetail.getKey().getName());
                assertThat(jobGroup).isEqualTo(actualJobDetail.getKey().getGroup());
                assertThat(jobName).isEqualTo(actualJobDetail.getDescription());
                assertThat(actualJobDetail.getJobDataMap().containsKey(JobDataKeys.PAYLOAD)).isTrue();
                assertThat("serialized-payload").isEqualTo(actualJobDetail.getJobDataMap().get(JobDataKeys.PAYLOAD));

                Trigger actualTrigger = triggerCaptor.getValue();
                assertThat(actualTrigger.getStartTime().toInstant().toEpochMilli()).isEqualTo(triggerAt.toInstant().toEpochMilli());
            }
        ).doesNotThrowAnyException();
    }

    @Test
    public void schedule_wraps_exception_from_client_deserializer() {

        assertThatThrownBy(
            () -> {

                String jobGroup = "test-job-group";
                String jobName = "test-job";
                String jobPayload = "payload";
                ZonedDateTime triggerAt = ZonedDateTime.now();

                Job<String> job = new Job<>(
                    jobGroup,
                    jobName,
                    jobPayload,
                    triggerAt
                );

                when(jobClassMapper.getJobMapping(String.class)).thenReturn(jobClassMapping);
                doThrow(RuntimeException.class)
                    .when(jobClassMapping)
                    .serialize(jobPayload);

                quartzJobScheduler.schedule(job);
            }
        ).hasMessage("Error while scheduling job")
            .isExactlyInstanceOf(JobException.class);
    }

    @Test
    public void schedule_throws_when_quartz_fails() {

        assertThatThrownBy(
            () -> {

                String jobGroup = "test-job-group";
                String jobName = "test-job";
                String jobPayload = "payload";
                ZonedDateTime triggerAt = ZonedDateTime.now();

                when(jobClassMapper.getJobMapping(String.class)).thenReturn(jobClassMapping);
                when(jobClassMapping.serialize(jobPayload)).thenReturn("serialized-payload");

                doThrow(RuntimeException.class)
                    .when(scheduler)
                    .scheduleJob(any(), any());

                Job<String> job = new Job<>(jobGroup, jobName, jobPayload, triggerAt);
                quartzJobScheduler.schedule(job);
            }
        ).hasMessage("Error while scheduling job")
            .isExactlyInstanceOf(JobException.class);
    }
}
