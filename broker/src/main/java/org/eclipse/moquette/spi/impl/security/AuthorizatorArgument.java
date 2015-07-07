/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.moquette.spi.impl.security;

import org.eclipse.moquette.proto.messages.PublishMessage;
import org.eclipse.moquette.server.ServerChannel;
import org.eclipse.moquette.server.netty.NettyChannel;

/**
 *
 * @author Luca Capra <luca.capra@gmail.com>
 */
public class AuthorizatorArgument {
    
    protected ServerChannel session;
    protected PublishMessage msg;
    
    public AuthorizatorArgument(ServerChannel session, PublishMessage msg) {
        this.session = session;
        this.msg = msg;
    }

    public ServerChannel getSession() {
        return session;
    }

    public PublishMessage getMsg() {
        return msg;
    }

    public String getUsername() {
        return (String) session.getAttribute(NettyChannel.ATTR_KEY_USERNAME);
    }
    
    public String getClientID() {
        return (String) session.getAttribute(NettyChannel.ATTR_KEY_CLIENTID);
    }
    
    public String getTopic() {
        return msg.getTopicName();
    }

}
