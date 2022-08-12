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
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import org.jooq.lambda.function.CheckedConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Charsets.UTF_8;

@Parameters(separators = "=", commandDescription = "Gets the key")
class CommandGet implements CheckedConsumer<Client> {
    @Parameter(arity = 1, description = "<key>")
    String key = "";

    @Parameter(names = "--rev", description = "Specify the kv revision")
    Long rev = 0L;

    @Override
    public void accept(Client client) throws Exception {
//        ByteSequence key = ByteSequence.from("\0".getBytes());
//        GetOption option = GetOption.newBuilder()
//                .withSortField(GetOption.SortTarget.KEY)
//                .withSortOrder(GetOption.SortOrder.DESCEND)
//                .withRange(key)
//                .build();


//        key = "foo";
//        GetResponse getResponse = client.getKVClient()
//            .get(ByteSequence.from(key, UTF_8), GetOption.newBuilder().withRevision(rev).build()).get();
//        GetResponse getResponse = client.getKVClient()
//            .get(key, option).get();
//
//        if (getResponse.getKvs().isEmpty()) {
//            // key does not exist
//            return;
//        }
//
//        System.out.println(key);
//        System.out.println(getResponse.getKvs().get(0).getValue().toString(UTF_8));

        try {
            ByteSequence key = ByteSequence.from("\0", UTF_8);
            GetOption option = GetOption.newBuilder()
                    .withSortField(GetOption.SortTarget.KEY)
                    .withSortOrder(GetOption.SortOrder.DESCEND)
                    .withRevision(0)
                    .withRange(key)
                    .build();

            CompletableFuture<GetResponse> futureResponse =
                    client.getKVClient().get(key, option);

            GetResponse response = futureResponse.get();
            if (response.getKvs().isEmpty()) {
                System.out.println("Failed to retrieve any key.");
//                return null;
            }


            Map<String, String> keyValueMap = new HashMap<>();
            for (KeyValue kv : response.getKvs()) {
                keyValueMap.put(kv.getKey().toString(),
                        kv.getValue().toString());
            }

            System.out.println("Retrieved " + response.getKvs().size() + " keys.");
            System.out.println(keyValueMap.toString());

//            return keyValueMap;

        } catch (Exception e) {
            throw new Exception(
                    "Failed to retrieve any key.", e);
        }
    }
}
