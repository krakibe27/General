/**
 * Copyright 2017 the original author or authors.
 *
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
package org.glowroot.agent.plugin.httpclient;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HttpServer extends NanoHTTPD {

    public HttpServer() {
        super(0);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            // parseBody() must be called due to https://github.com/NanoHttpd/nanohttpd/issues/427
            session.parseBody(new HashMap<String, String>());
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (ResponseException e) {
            throw new RuntimeException();
        }
        Response response = newFixedLengthResponse(Status.OK, "text/plain", null, 0);
        response.addHeader("Connection", "Close");
        return response;
    }

    @Override
    public void stop() {
        asyncRunner.closeAll();
        super.stop();
    }
}
