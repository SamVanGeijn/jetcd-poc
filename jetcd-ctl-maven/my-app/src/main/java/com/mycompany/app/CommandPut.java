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
import org.jooq.lambda.function.CheckedConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Parameters(separators = "=", commandDescription = "Puts the given key into the store")
class CommandPut implements CheckedConsumer<Client> {
    @Parameter(arity = 2, description = "<key> <value>")
    List<String> keyValue;

    @Override
    public void accept(Client client) throws Exception {
        keyValue = new ArrayList<>();
        keyValue.add("foo");
        keyValue.add("bar");
        client.getKVClient()
            .put(ByteSequence.from(keyValue.get(0), Charsets.UTF_8), ByteSequence.from(keyValue.get(1), Charsets.UTF_8)).get();

        System.out.println("OK");
    }
}
