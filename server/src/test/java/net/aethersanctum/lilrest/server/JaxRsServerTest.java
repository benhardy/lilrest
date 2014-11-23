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

import com.google.inject.servlet.ServletModule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Basic smoke test
 */
public class JaxRsServerTest {

    @ClassRule
    public static ExternalResource serverRule = new ExternalResource() {

        private final TestServer server = new TestServer();

        @Override
        protected void before() throws Throwable {
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
        final String link = "http://localhost:8080/hello";
        fetchExpecting(expected, link);
    }

    @Test
    public void retrieveJson() throws Exception {
        final String expected = "{\"number\":42}";
        final String link = "http://localhost:8080/pojo";
        fetchExpecting(expected, link);
    }

    @Test
    public void healthReturnsStatusOK() throws Exception {
        final String expected = "{\"status\":\"OK\"}";
        final String link = "http://localhost:8080/api/health";
        fetchExpecting(expected, link);
    }

    private void fetchExpecting(final String expected, final String link) throws IOException {
        final URL url = new URL(link);
        try (final InputStreamReader is = new InputStreamReader(url.openStream());
             final Scanner scanner = new Scanner(is)) {
            final String line = scanner.nextLine();
            assertEquals(expected, line);
        }
    }

    public static class TestServer extends JaxRsServer {

        @Override
        protected ServletModule getMainModule() {
            return new ServletModule() {
                @Override
                public void configureServlets() {
                    bind(TestResource.class).asEagerSingleton();
                }
            };
        }
    }

    public static class SomePojo {
        private final int number;

        public SomePojo(final int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
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
            return new SomePojo(42);
        }
    }

}
