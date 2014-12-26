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
package net.aethersanctum.lilrest.config;

import org.skife.config.ConfigurationObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Allows Guice modules to bind a configuration object created by
 * Config Magic (skife).
 */
@Singleton
public final class ConfigFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigFactory.class);

    private final Properties configProperties;
    private final ConfigurationObjectFactory factory;

    public <T> T extract(@Nonnull Class<T> configClass) {
        LOG.info("extracting {}", configClass.getCanonicalName());
        return factory.build(configClass);
    }

    public ConfigFactory() {
        this.configProperties = findConfigProperties();
        this.factory = new ConfigurationObjectFactory(configProperties);
        LOG.info("loaded {} config properties", configProperties.size());
    }

    private static Properties findConfigProperties() {
        final String useConfig = System.getProperty("useConfig");
        if (useConfig != null) {
            return loadProperties(useConfig);
        }
        return System.getProperties();
    }

    private static Properties loadProperties(final String location) {
        try(InputStream inStream = propertiesStream(location)) {
            final Properties props = new Properties();
            props.load(inStream);
            return props;
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    private static InputStream propertiesStream(final String location) throws FileNotFoundException {
        final File confFile = new File(location);
        final InputStream inStream;
        if (confFile.canRead()) {
            inStream = new FileInputStream(new File(location));
        } else {
            inStream = ConfigFactory.class.getClassLoader().getResourceAsStream(location);
            if (inStream == null) {
                throw new IllegalArgumentException("Couldn't find config file " + location
                        + " in filesystem or classpath.");
            }
        }
        return inStream;
    }
}

