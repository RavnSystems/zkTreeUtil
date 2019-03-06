/**
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

package org.apache.jute;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

/**
 *
 */
public class SimpleXmlOutputArchive implements OutputArchive {

    private PrintStream stream;
    
    private int indent = 0;
    
    private Stack<String> compoundStack;
    
    private void putIndent() {
        StringBuilder sb = new StringBuilder("");
        for (int idx = 0; idx < indent; idx++) {
            sb.append("  ");
        }
        stream.print(sb.toString());
    }
    
    private void addIndent() {
        indent++;
    }
    
    private void closeIndent() {
        indent--;
    }
    
    private void printBeginEnvelope(String tag) {
        stream.print("<"+tag+">");
    }
    
    private void printEndEnvelope(String tag) {
        stream.println("</"+tag+">");
    }
    
    private void insideVector(String tag) {
        printBeginEnvelope(tag);
        compoundStack.push("vector");
    }
    
    private void outsideVector(String tag) throws IOException {
        String s = compoundStack.pop();
        if (!"vector".equals(s)) {
            throw new IOException("Error serializing vector.");
        }
        printEndEnvelope(tag);
    }
    
    private void insideMap(String tag) {
        printBeginEnvelope(tag);
        compoundStack.push("map");
    }
    
    private void outsideMap(String tag) throws IOException {
        String s = compoundStack.pop();
        if (!"map".equals(s)) {
            throw new IOException("Error serializing map.");
        }
        printEndEnvelope(tag);
    }
    
    private void insideRecord(String tag) {
        printBeginEnvelope(tag);
        compoundStack.push("struct");
    }
    
    private void outsideRecord(String tag) throws IOException {
        String s = compoundStack.pop();
        if (!"struct".equals(s)) {
            throw new IOException("Error serializing record.");
        }
        printEndEnvelope(tag);
    }
    
    static SimpleXmlOutputArchive getArchive(OutputStream strm) {
        return new SimpleXmlOutputArchive(strm);
    }
    
    /** Creates a new instance of XmlOutputArchive */
    public SimpleXmlOutputArchive(OutputStream out) {
        stream = new PrintStream(out);
        compoundStack = new Stack<String>();
    }
    
    public void writeByte(byte b, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(Byte.toString(b));
        printEndEnvelope(tag);
    }
    
    public void writeBool(boolean b, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(b ? "true" : "false");
        printEndEnvelope(tag);
    }
    
    public void writeInt(int i, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(Integer.toString(i));
        printEndEnvelope(tag);
    }
    
    public void writeLong(long l, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(Long.toString(l));
        printEndEnvelope(tag);
    }
    
    public void writeFloat(float f, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(Float.toString(f));
        printEndEnvelope(tag);
    }
    
    public void writeDouble(double d, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(Double.toString(d));
        printEndEnvelope(tag);
    }
    
    public void writeString(String s, String tag) throws IOException {
        printBeginEnvelope(tag);
        stream.print(Utils.toXMLString(s));
        printEndEnvelope(tag);
    }
    
    public void writeBuffer(byte buf[], String tag)
    throws IOException {
        printBeginEnvelope(tag);
        stream.print(Utils.toXMLBuffer(buf));
        printEndEnvelope(tag);
    }
    
    public void writeRecord(Record r, String tag) throws IOException {
        r.serialize(this, tag);
    }
    
    public void startRecord(Record r, String tag) throws IOException {
        insideRecord(tag);
        addIndent();
    }
    
    public void endRecord(Record r, String tag) throws IOException {
        closeIndent();
        putIndent();
        outsideRecord(tag);
    }
    
    public void startVector(List v, String tag) throws IOException {
        insideVector(tag);
        stream.print("<array>\n");
        addIndent();
    }
    
    public void endVector(List v, String tag) throws IOException {
        closeIndent();
        putIndent();
        stream.print("</array>");
        outsideVector(tag);
    }
    
    public void startMap(TreeMap v, String tag) throws IOException {
        insideMap(tag);
        stream.print("<array>\n");
        addIndent();
    }
    
    public void endMap(TreeMap v, String tag) throws IOException {
        closeIndent();
        putIndent();
        stream.print("</array>");
        outsideMap(tag);
    }

}
