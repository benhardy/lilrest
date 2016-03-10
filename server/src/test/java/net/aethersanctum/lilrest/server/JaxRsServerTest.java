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
package net.aethersanctum.lilrest.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

/**
 * Basic smoke test
 */
public class JaxRsServerTest {

    public static final int SERVER_PORT = 8080;
    public static final String SERVER_URL_BASE = "http://localhost:" + SERVER_PORT;

    @ClassRule
    public static ExternalResource serverRule = new ExternalResource() {

        private final TestServer server = new TestServer();

        @Override
        protected void before() throws Throwable {
            // don't want to call join() here, keep my thread separate to server's.
            server.getJettyServer().start();
        }

        @Override
        protected void after() {
            try {
                server.getJettyServer().stop();
            } catch (Exception e) {
                // trouble shutting down
            }
        }
    };

    @Test
    public void retrieveString() throws Exception {
        final String expected = "Hello";
        fetchExpecting(expected, "/hello");
    }

    @Test
    public void retrieveJson() throws Exception {
        final String expected = jsonFixQuotes("{'number':42,'name':'Fred'}");
        fetchExpecting(expected, "/pojo");
    }

    @Test
    public void healthReturnsStatusOK() throws Exception {
        final String expected = jsonFixQuotes("{'status':'OK'}");
        fetchExpecting(expected, "/api/health");
    }

    private String jsonFixQuotes(String json) {
        return json.replaceAll("'", "\"");
    }

    private void fetchExpecting(final String expected, final String relativeLink) throws IOException {
        final URL url = new URL(SERVER_URL_BASE + relativeLink);
        try (final InputStreamReader is = new InputStreamReader(url.openStream());
             final Scanner scanner = new Scanner(is)) {
            final String line = scanner.nextLine();
            assertEquals(expected, line);
        }
    }

    public static class SomePojo {
        private final int number;
        private final Optional<String> name;

        public SomePojo(final int number, final Optional<String> name) {
            this.number = number;
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public Optional<String> getName() { return name; }

        public Optional<String> nothing() { return Optional.empty(); }
    }

    @Singleton
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public static class TestResource {
        @Path("/hello")
        @GET
        public String hello() {
            return "Hello";
        }

        @Path("/pojo")
        @GET
        public SomePojo somePojo() {
            return new SomePojo(42, Optional.of("Fred"));
        }

     }

}
