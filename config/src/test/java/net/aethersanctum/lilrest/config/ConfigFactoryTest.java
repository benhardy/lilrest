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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructFailConfigNotFound() {
        System.setProperty("useConfig", "src/test/resources/nonexistent.properties");
        ConfigFactory factory = new ConfigFactory();
    }

    @Test
    public void constructFromFile() {
        System.setProperty("useConfig", "src/test/resources/test.properties");
        ConfigFactory factory = new ConfigFactory();
    }

    @Test
    public void constructFromClasspath() {
        System.setProperty("useConfig", "test.properties");
        ConfigFactory factory = new ConfigFactory();
    }

    @Test
    public void extraction() {
        System.setProperty("useConfig", "test.properties");
        ConfigFactory factory = new ConfigFactory();
        TestConfig config = factory.extract(TestConfig.class);
        assertNotNull(config);
    }

    @Test
    public void retrieval() {
        System.setProperty("useConfig", "test.properties");
        ConfigFactory factory = new ConfigFactory();
        TestConfig config = factory.extract(TestConfig.class);
        String value = config.someKey();
        assertEquals("somevalue", value);
    }

}
