/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.dubbo.sample.tri.stub;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.sample.tri.Greeter;
import org.apache.dubbo.sample.tri.util.TriSampleClient;
import org.apache.dubbo.sample.tri.util.TriSampleConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TriStubClient extends TriSampleClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TriStubClient.class);

    public TriStubClient() {
        this(false, null);
    }

    public TriStubClient(boolean direct, String directUrl) {
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        ReferenceConfig<Greeter> ref = new ReferenceConfig<>();
        ref.setInterface(Greeter.class);
        ref.setProtocol(CommonConstants.TRIPLE);
        ref.setProxy(CommonConstants.NATIVE_STUB);
        ref.setLoadbalance("roundrobin");
        ref.setTimeout(3000);

        if (!direct) {
            bootstrap.registry(new RegistryConfig(TriSampleConstants.ZK_ADDRESS));
        } else {
            ref.setUrl(directUrl);
        }
        bootstrap.application(new ApplicationConfig("tri-stub-consumer"))
                .reference(ref)
                .start();
        setGreeter(ref.get());
        setClientName("tri-stub");
    }

    public static void main(String[] args) throws IOException {
        final TriStubClient consumer = new TriStubClient();
        try {
            while (true) {
                consumer.unary();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        consumer.stream();
//        consumer.serverStream();
//        System.in.read();
    }

}