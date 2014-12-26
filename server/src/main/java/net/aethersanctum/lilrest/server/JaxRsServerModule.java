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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import net.aethersanctum.lilrest.config.ConfigFactory;
import net.aethersanctum.lilrest.health.HealthModule;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Basic module for a JAX-RS Server. Binds all the RESTEasy and Jackson
 * stuff we need to serve JSON over REST. Sets up Jetty.
 */
public final class JaxRsServerModule extends AbstractModule {
    @Override
    public void configure() {
        binder().requireExplicitBindings();
        bind(GuiceResteasyBootstrapServletContextListener.class).in(Scopes.SINGLETON);

        bind(ObjectMapper.class).toProvider(this::customMapper);

        bind(GuiceFilter.class);
        bind(HttpServletDispatcher.class).in(Scopes.SINGLETON);

        install(new HealthModule());
    }

    @Provides
    @Singleton
    public JacksonJsonProvider jacksonJsonProvider(ObjectMapper mapper){
        JacksonJsonProvider p = new JacksonJsonProvider();
        p.setMapper(mapper);
        return p;
    }

    public ObjectMapper customMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Provides
    public JaxRsServerConfig serverConfiguration(@Nonnull final ConfigFactory factory) {
        return factory.extract(JaxRsServerConfig.class);
    }

    @Provides
    private Server assembleJettyServer(JaxRsServerConfig config,
                                       ServletContextHandler context) {
        final QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(config.maxThreads());

        final Server server = new Server(threadPool);
        server.setHandler(context);

        // Setup JMX
        //MBeanContainer mbContainer=new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        //server.addBean(mbContainer);

        setupConnectors(config, server);
        return server;
    }

    private void setupConnectors(final JaxRsServerConfig config, final Server server) {
        final ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setHost("localhost");
        httpConnector.setPort(config.port());
        httpConnector.setIdleTimeout(config.idleTimeout().getMillis());

        server.setConnectors(new Connector[]{httpConnector});
    }

    @Provides
    public ServletContextHandler servletContext(GuiceResteasyBootstrapServletContextListener resteasyListener,
                                                GuiceFilter guiceFilter,
                                                GuiceServletContextListener guiceServletContextListener) {
        final FilterHolder guiceFilterHolder = new FilterHolder(guiceFilter);
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addFilter(guiceFilterHolder, "/*", EnumSet.allOf(DispatcherType.class));
        context.addEventListener(resteasyListener);
        context.addEventListener(guiceServletContextListener);
        return context;
    }

    @Provides
    private GuiceServletContextListener getGuiceServletContextListener(final Injector injector) {
        return new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injector;
            }
        };
    }
}
