/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.aethersanctum.lilrest.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 */
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public final class HealthResource {
    private static final Logger LOG = LoggerFactory.getLogger(HealthResource.class);
    private HealthService healthService;

    @Inject
    HealthResource(@Nonnull final HealthService healthService) {
        LOG.debug("Creating HealthResource");
        this.healthService = healthService;
    }

    @GET
    @Path("/health")
    @Nonnull
    public Health health() {
        return healthService.getCurrentHealth();
    }

    @GET
    @Path("/ping")
    @Nonnull
    public Ping ping(@Context HttpServletRequest req) {
        return Ping.of(req);
    }
}
