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

import org.apache.zookeeper.data.Stat;

public class ZNode {
    private final String name;
    private final String path;
    private final byte[] data;
    private final Stat stat;
    private final boolean has_children;

    public ZNode(String name, String path, byte[] data, Stat stat, boolean has_children) {
        this.name = name;
        this.path = path;
        this.data = data!=null? data.clone(): null;
        this.stat = stat;
        this.has_children = has_children;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path.startsWith("/")? path.substring(1): path;
    }

    public byte[] getData() {
        return data;
    }

    public Stat getStat() {
        return stat;
    }

    public boolean hasChildren() {
        return has_children;
    }

    public String readData() {
        return data!=null && data.length >0 ? new String(data): "";
    }
}
