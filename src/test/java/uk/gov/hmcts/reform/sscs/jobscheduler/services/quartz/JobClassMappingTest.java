package uk.gov.hmcts.reform.sscs.jobscheduler.services.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.sscs.jobscheduler.services.JobPayloadSerializer;

public class JobClassMappingTest {

    private JobPayloadSerializer<String> jobPayloadSerializer;

    @BeforeEach
    public void setUp() {
        jobPayloadSerializer = mock(JobPayloadSerializer.class);
    }

    @Test
    public void mappingCanHandlePayloadByClass() {
        JobClassMapping<String> jobMapping = new JobClassMapping<>(String.class, jobPayloadSerializer);

        boolean canHandle = jobMapping.canHandle(String.class);

        assertThat(canHandle).isTrue();
    }

    @Test
    public void mappingCannotHandlePayloadByClass() {
        JobClassMapping<String> jobMapping = new JobClassMapping<>(String.class, jobPayloadSerializer);

        boolean canHandle = jobMapping.canHandle(Integer.class);

        assertThat(canHandle).isFalse();
    }

    @Test
    public void serialize() {
        JobClassMapping<String> jobMapping = new JobClassMapping<>(String.class, jobPayloadSerializer);

        String payload = "payload";
        jobMapping.serialize(payload);
        verify(jobPayloadSerializer).serialize(payload);
    }
}
