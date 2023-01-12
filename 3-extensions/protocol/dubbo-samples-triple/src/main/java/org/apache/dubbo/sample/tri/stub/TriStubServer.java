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
import org.apache.dubbo.common.context.Lifecycle;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.sample.tri.Greeter;
import org.apache.dubbo.sample.tri.util.EmbeddedZooKeeper;
import org.apache.dubbo.sample.tri.util.TriSampleConstants;

import java.io.IOException;

public class TriStubServer implements Lifecycle {
    private final int port;

    public TriStubServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        TriStubServer server = new TriStubServer(TriSampleConstants.SERVER_PORT);
        server.initialize();
        server.start();
        System.out.println("Dubbo triple stub server started, port=" + TriSampleConstants.SERVER_PORT);
        System.in.read();
    }

    @Override
    public void initialize() throws IllegalStateException {
        new EmbeddedZooKeeper(TriSampleConstants.ZK_PORT, false).start();
    }

    @Override
    public void start() throws IllegalStateException {

        // protocol
        final ProtocolConfig protocolConfig = new ProtocolConfig(CommonConstants.TRIPLE, port);
        protocolConfig.setHost(TriSampleConstants.HOST);
        final RegistryConfig registryConfig = new RegistryConfig(TriSampleConstants.ZK_ADDRESS);
        ServiceConfig<Greeter> service = new ServiceConfig<>();
        service.setInterface(Greeter.class);
        service.setRef(new GreeterImpl("tri-stub"));
        service.setRegistry(registryConfig);
        final MetadataReportConfig metadataReportConfig = new MetadataReportConfig();
        metadataReportConfig.setProtocol(CommonConstants.DEFAULT_PROTOCOL);
        metadataReportConfig.setAddress(TriSampleConstants.ZK_ADDRESS);
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        final ApplicationConfig applicationConfig = new ApplicationConfig("tri-stub-server");
        applicationConfig.setQosEnable(false);
        applicationConfig.setMetadataServiceProtocol(CommonConstants.DEFAULT_PROTOCOL);
        bootstrap.application(applicationConfig)
                .registry(registryConfig)
                .protocol(protocolConfig)
                .metadataReport(metadataReportConfig)
                .service(service)
                .start();
    }

    @Override
    public void destroy() throws IllegalStateException {
        DubboBootstrap.getInstance().stop();
    }
}
