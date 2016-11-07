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
package net.aethersanctum.lilrest.examples.simple;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class AddressOperations {
    private static final int HTTP_UNPROCESSABLE_ENTITY = 422;

    private static final Logger LOG = LoggerFactory.getLogger(AddressOperations.class);

    @GET
    @Path("/hello")
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/address/{name}")
    public Address someonesAddress(@PathParam("name") String name) {
        return new Address(
                LocalDateTime.now(),
                "123 Credibility St",
                Optional.of("Moonshine Estate"),
                Optional.of("APT 302"),
                "Chattanooga",
                Optional.of("Hamilton"),
                Optional.of("37405"),
                "USA"
        );
    }

    @POST
    @Path("/address")
    public Response someonesAddress(Address newAddress) {
        LOG.info("new Address uploaded: {}", newAddress);
        if (newAddress.getStreetAddress1() != null
                && newAddress.getCity() != null
                && newAddress.getCountry() != null) {
            return Response.accepted().build();
        }
        return Response.status(HTTP_UNPROCESSABLE_ENTITY).build();
    }
}
