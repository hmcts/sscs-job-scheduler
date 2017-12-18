package uk.gov.hmcts.reform.jobscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.CachedServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

@Configuration
@Lazy
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class ServiceTokenGeneratorConfiguration {

    @Bean
    public AuthTokenGenerator cachedServiceAuthTokenGenerator(
        @Value("${idam.s2s-auth.secret}") final String secret,
        @Value("${idam.s2s-auth.microservice}") final String microService,
        @Value("${idam.s2s-auth.tokenTimeToLiveInSeconds}") final int ttl,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return new CachedServiceAuthTokenGenerator(
            new ServiceAuthTokenGenerator(
                secret,
                microService,
                serviceAuthorisationApi
            ),
            ttl
        );
    }
}