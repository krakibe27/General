/*
 * Copyright 2018 the original author or authors.
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
package org.glowroot.agent.plugin.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.glowroot.agent.plugin.api.AsyncTraceEntry;
import org.glowroot.agent.plugin.api.AuxThreadContext;
import org.glowroot.agent.plugin.api.TraceEntry;
import org.glowroot.agent.plugin.api.checker.Nullable;

public class CallbackWrapper implements Callback {

    private final Callback delegate;
    private final AsyncTraceEntry asyncTraceEntry;
    private final AuxThreadContext auxContext;

    public CallbackWrapper(Callback delegate, AsyncTraceEntry asyncTraceEntry,
            AuxThreadContext auxContext) {
        this.delegate = delegate;
        this.asyncTraceEntry = asyncTraceEntry;
        this.auxContext = auxContext;
    }

    @Override
    public void onCompletion(@Nullable RecordMetadata metadata, @Nullable Exception exception) {
        if (exception == null) {
            asyncTraceEntry.end();
        } else {
            asyncTraceEntry.endWithError(exception);
        }
        TraceEntry traceEntry = auxContext.start();
        try {
            delegate.onCompletion(metadata, exception);
        } catch (Throwable t) {
            traceEntry.endWithError(t);
            throw rethrow(t);
        }
        traceEntry.end();
    }

    private static RuntimeException rethrow(Throwable t) {
        CallbackWrapper.<RuntimeException>throwsUnchecked(t);
        throw new AssertionError();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwsUnchecked(Throwable t) throws T {
        throw (T) t;
    }
}
