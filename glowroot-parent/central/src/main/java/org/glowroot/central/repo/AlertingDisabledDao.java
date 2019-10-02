/*
 * Copyright 2019 the original author or authors.
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
package org.glowroot.central.repo;

import java.util.Date;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.google.common.primitives.Ints;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.glowroot.central.util.Session;
import org.glowroot.common.util.Clock;
import org.glowroot.common2.repo.AlertingDisabledRepository;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AlertingDisabledDao implements AlertingDisabledRepository {

    private final Session session;
    private final Clock clock;

    private final PreparedStatement insertPS;
    private final PreparedStatement deletePS;
    private final PreparedStatement readPS;

    AlertingDisabledDao(Session session, Clock clock) throws Exception {
        this.session = session;
        this.clock = clock;

        session.createTableWithLCS("create table if not exists alerting_disabled (agent_rollup_id"
                + " varchar, disabled_until_time timestamp, primary key (agent_rollup_id))");

        insertPS = session.prepare("insert into alerting_disabled (agent_rollup_id,"
                + " disabled_until_time) values (?, ?) using ttl ?");
        deletePS = session.prepare("delete from alerting_disabled where agent_rollup_id = ?");
        readPS = session.prepare(
                "select disabled_until_time from alerting_disabled where agent_rollup_id = ?");
    }

    @Override
    public @Nullable Long getAlertingDisabledUntilTime(String agentRollupId) throws Exception {
        BoundStatement boundStatement = readPS.bind();
        boundStatement.setString(0, agentRollupId);
        Row row = session.read(boundStatement).one();
        if (row == null) {
            return null;
        }
        Date timestamp = row.getTimestamp(0);
        return timestamp == null ? null : timestamp.getTime();
    }

    @Override
    public void setAlertingDisabledUntilTime(String agentRollupId, @Nullable Long disabledUntilTime)
            throws Exception {
        if (disabledUntilTime == null) {
            BoundStatement boundStatement = deletePS.bind();
            boundStatement.setString(0, agentRollupId);
            session.write(boundStatement);
        } else {
            BoundStatement boundStatement = insertPS.bind();
            int i = 0;
            boundStatement.setString(i++, agentRollupId);
            boundStatement.setTimestamp(i++, new Date(disabledUntilTime));
            boundStatement.setInt(i++, Ints.saturatedCast(
                    MILLISECONDS.toSeconds(disabledUntilTime - clock.currentTimeMillis())));
            session.write(boundStatement);
        }
    }
}
