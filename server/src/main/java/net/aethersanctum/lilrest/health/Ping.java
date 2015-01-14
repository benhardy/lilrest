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

import javax.servlet.http.HttpServletRequest;

public class Ping {

    private final String requestUrl;
    private final String requestUri;
    private final String remoteHost;
    private final String remoteAddr;
    private final int remotePort;
    private final String remoteUser;

    private Ping(final HttpServletRequest item) {
        this.requestUri = item.getRequestURI();
        this.requestUrl = item.getRequestURL().toString();
        this.remoteAddr = item.getRemoteAddr();
        this.remoteHost = item.getRemoteHost();
        this.remoteUser = item.getRemoteUser();
        this.remotePort = item.getRemotePort();
    }

    public static Ping of(HttpServletRequest req) {
        return new Ping(req);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteUser() {
        return remoteUser;
    }
}
