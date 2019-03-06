/**
 * Copyright 2014 Aleksey Dobrunov
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dobrunov.zktreeutil;

import org.apache.jute.OutputArchive;
import org.apache.jute.SimpleXmlOutputArchive;
import org.apache.zookeeper.data.Stat;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Export zookeeper tree to file system
 */
public class zkExportToFS implements Job {

    private String zkServer;
    private final Path outputDir;
    private String start_znode;
    private final org.slf4j.Logger logger;


    public zkExportToFS(String zkServer, String znode, Path outputDir) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.outputDir = outputDir;
        this.start_znode = znode;
    }

    public void go() {
        zkDumpZookeeper dump = new zkDumpZookeeper(zkServer, start_znode);
        try {
            TreeNode<ZNode> zktree = dump.getZktree();
            logger.info("begin write zookeeper tree to folder " + outputDir);
            for (TreeNode<ZNode> znode : zktree) {
                if (znode.data.hasChildren()) {
                    final Path nodePath = outputDir.resolve(znode.data.getPath());
                    Files.createDirectories(nodePath);
                }
                writeZnode(znode.data);
                if(znode.data.getStat().getEphemeralOwner()>0) writeMetadata(znode.data);
            }
            logger.info("end write zookeeper tree to folder " + outputDir);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void writeZnode(ZNode znode) {
        final String str = znode.readData();
        final String outFile = znode.hasChildren() ? "_znode" : znode.getPath();
        try{
            Files.write(outputDir.resolve(outFile), str.getBytes());
        } catch (Exception e) {
            logger.error("Unable to write zookeeper node", e);
        }
    }

    private void writeMetadata(ZNode zNode){
        final Stat metadata = zNode.getStat();
        final String outFile = zNode.getPath().concat(".metadata.xml");
        try(final OutputStream outputStream = Files.newOutputStream(outputDir.resolve(outFile))){
            final OutputArchive outputArchive = new SimpleXmlOutputArchive(outputStream);
            metadata.serialize(outputArchive, "metadata");
        } catch (Exception e){
            logger.error("Unable to write medata node", e);
        }
    }

}
