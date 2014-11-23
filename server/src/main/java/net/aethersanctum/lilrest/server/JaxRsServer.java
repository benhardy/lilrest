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

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import net.aethersanctum.lilrest.config.ConfigModule;
import org.eclipse.jetty.server.Server;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.inject.Inject;

/**
 * Base JAX-RS server with all the goodies set up in the JaxRsServerModule.
 * Extend this class with your own server type and implement getMainModule.
 * Have a main() in your class that creates a new instance of your server
 * and calls start() on it.
 */
public abstract class JaxRsServer {

    @Inject
    private Server jettyServer;

    protected JaxRsServer() {

        final Injector injector = Guice.createInjector(
                new ConfigModule(),
                new JaxRsServerModule(),
                getMainModule(),
                new ServletModule() {
                    @Override
                    public void configureServlets() {
                        serve("/*").with(HttpServletDispatcher.class);
                    }
                }
        );
        injector.injectMembers(this);
    }

    protected abstract ServletModule getMainModule();

    public final void start() throws Exception {
        jettyServer.start();
        jettyServer.join();
    }

    @VisibleForTesting
    Server getJettyServer()   {
        return jettyServer;
    }
}
