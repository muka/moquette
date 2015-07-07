/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.moquette.spi.impl;

import org.eclipse.moquette.proto.messages.ConnectMessage;
import org.eclipse.moquette.proto.messages.DisconnectMessage;
import org.eclipse.moquette.proto.messages.PublishMessage;
import org.eclipse.moquette.proto.messages.SubscribeMessage;
import org.eclipse.moquette.proto.messages.UnsubscribeMessage;
import org.eclipse.moquette.server.ServerChannel;

/**
 *
 * @author Luca Capra <luca.capra@gmail.com>
 */
public interface IMessagingListener {
    
    public void onConnect(ServerChannel session, ConnectMessage msg);
    public void onDisconnect(ServerChannel session, DisconnectMessage msg);
    
    public void onPublish(ServerChannel session, PublishMessage msg);
    
    public void onSubscribe(ServerChannel session, SubscribeMessage msg);
    public void onUnsubscribe(ServerChannel session, UnsubscribeMessage msg);
    
}
