/*
 * Copyright 2016-2021 The jetcd authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch.Watcher;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.jooq.lambda.function.CheckedConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Parameters(separators = "=", commandDescription = "Watches events stream for a key")
class CommandWatch implements CheckedConsumer<Client> {
    @Parameter(arity = 1, description = "<key>")
    String key;

    @Parameter(names = "--rev", description = "Revision to start watching")
    Long rev = 0L;

    @Parameter(names = { "-m", "--max-events" }, description = "the maximum number of events to receive")
    Integer maxEvents = Integer.MAX_VALUE;

    @Override
    public void accept(Client client) throws Exception {
        maxEvents = 5;
        CountDownLatch latch = new CountDownLatch(maxEvents);
        Watcher watcher = null;

        try {
            ByteSequence key = ByteSequence.from("\0".getBytes());
            WatchOption watchOpts = WatchOption.newBuilder()
//                    .withRevision()
                    .withRange(key)
                    .build();

            watcher = client.getWatchClient().watch(key, watchOpts, response -> {
                Map<String, String> res = response.getEvents().stream()
                        .collect(Collectors.toMap(a -> a.getKeyValue().getKey().toString(), a -> a.getKeyValue().getValue().toString()));
                for (WatchEvent event : response.getEvents()) {
                    System.out.println(String.format("type=%s, key=%s, value=%s", event.getEventType().toString(),
                        Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(Charsets.UTF_8)).orElse(""),
                        Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(Charsets.UTF_8)).orElse("")));
                }

                latch.countDown();
            });

            latch.await();
        } catch (Exception e) {
            if (watcher != null) {
                watcher.close();
            }

            throw e;
        }
    }
}
