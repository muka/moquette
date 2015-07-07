/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package org.eclipse.moquette.spi.impl.security;

import io.netty.util.AttributeKey;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import org.eclipse.moquette.proto.messages.PublishMessage;
import org.eclipse.moquette.server.ServerChannel;
import org.eclipse.moquette.server.netty.NettyChannel;

import static org.junit.Assert.*;

/**
 * @author andrea
 */
public class AuthorizationsCollectorTest {

    private static final Authorization RW_ANEMOMETER = new Authorization("/weather/italy/anemometer");
    private static final Authorization R_ANEMOMETER = new Authorization("/weather/italy/anemometer", Authorization.Permission.READ);
    private static final Authorization W_ANEMOMETER = new Authorization("/weather/italy/anemometer", Authorization.Permission.WRITE);

    private AuthorizationsCollector authorizator;
    
    static AuthorizatorArgument createAuthorizatorArg(String topic, final String user, final String clientID) {
        
        PublishMessage m = new PublishMessage();
        m.setTopicName(topic);

        ServerChannel channel = new ServerChannel() {

            @Override
            public Object getAttribute(AttributeKey<Object> key) {
                
                if(key.equals(NettyChannel.ATTR_KEY_USERNAME)) {
                    return user;
                }
                if(key.equals(NettyChannel.ATTR_KEY_CLIENTID)) {
                    return clientID;
                }
                
                return null;
            }

            @Override
            public void setAttribute(AttributeKey<Object> key, Object value) {}

            @Override
            public void setIdleTime(int idleTime) {}

            @Override
            public void close(boolean immediately) {}

            @Override
            public void write(Object value) {}
        };
                
        return new AuthorizatorArgument(channel, m);
    }
    
    @Before
    public void setUp() {
        authorizator = new AuthorizationsCollector();
    }

    @Test
    public void testParseAuthLineValid() throws ParseException {
        Authorization authorization = authorizator.parseAuthLine("topic /weather/italy/anemometer");

        //Verify
        assertEquals(RW_ANEMOMETER, authorization);
    }

    @Test
    public void testParseAuthLineValid_read() throws ParseException {
        Authorization authorization = authorizator.parseAuthLine("topic read /weather/italy/anemometer");

        //Verify
        assertEquals(R_ANEMOMETER, authorization);
    }

    @Test
    public void testParseAuthLineValid_write() throws ParseException {
        Authorization authorization = authorizator.parseAuthLine("topic write /weather/italy/anemometer");

        //Verify
        assertEquals(W_ANEMOMETER, authorization);
    }

    @Test
    public void testParseAuthLineValid_readwrite() throws ParseException {
        Authorization authorization = authorizator.parseAuthLine("topic readwrite /weather/italy/anemometer");

        //Verify
        assertEquals(RW_ANEMOMETER, authorization);
    }


    @Test
    public void testParseAuthLineValid_topic_with_space() throws ParseException {
        Authorization expected = new Authorization("/weather/eastern italy/anemometer");
        Authorization authorization = authorizator.parseAuthLine("topic readwrite /weather/eastern italy/anemometer");

        //Verify
        assertEquals(expected, authorization);
    }

    @Test(expected = ParseException.class)
    public void testParseAuthLineValid_invalid() throws ParseException {
        authorizator.parseAuthLine("topic faker /weather/italy/anemometer");
    }

    @Test
    public void testCanWriteSimpleTopic() throws ParseException {
        authorizator.parse("topic write /sensors");

        //verify
        assertTrue(authorizator.canWrite(createAuthorizatorArg("/sensors", "", "")));
    }

    @Test
    public void testCanReadSimpleTopic() throws ParseException {
        authorizator.parse("topic read /sensors");

        //verify
        assertTrue(authorizator.canRead(createAuthorizatorArg("/sensors", "", "")));
    }

    @Test
    public void testCanReadWriteMixedSimpleTopic() throws ParseException {
        authorizator.parse("topic write /sensors");
        authorizator.parse("topic read /sensors/anemometer");

        //verify
        assertTrue(authorizator.canWrite(createAuthorizatorArg("/sensors", "", "")));
        assertFalse(authorizator.canRead(createAuthorizatorArg("/sensors", "", "")));
    }

    @Test
    public void testCanWriteMultiMatherTopic() throws ParseException {
        authorizator.parse("topic write /sensors/#");

        //verify
        assertTrue(authorizator.canWrite(createAuthorizatorArg("/sensors/anemometer/wind", "", "")));
    }

    @Test
    public void testCanWriteSingleMatherTopic() throws ParseException {
        authorizator.parse("topic write /sensors/+");

        //verify
        assertTrue(authorizator.canWrite(createAuthorizatorArg("/sensors/anemometer", "", "")));
    }

    @Test
    public void testCanWriteUserTopic() throws ParseException {
        authorizator.parse("user john");
        authorizator.parse("topic write /sensors");

        //verify
        assertTrue(authorizator.canWrite(createAuthorizatorArg("/sensors", "john", "")));
        assertFalse(authorizator.canWrite(createAuthorizatorArg("/sensors", "jack", "")));
    }

    @Test
    public void testPatternClientLineACL() throws ParseException {
        authorizator.parse("pattern read /weather/italy/%c");

        //Verify
        assertTrue(authorizator.canRead(createAuthorizatorArg("/weather/italy/anemometer1", "", "anemometer1")));
    }

    @Test
    public void testPatternClientAndUserLineACL() throws ParseException {
        authorizator.parse("pattern read /weather/%u/%c");

        //Verify
        assertTrue(authorizator.canRead(createAuthorizatorArg("/weather/italy/anemometer1", "italy", "anemometer1")));
    }
}