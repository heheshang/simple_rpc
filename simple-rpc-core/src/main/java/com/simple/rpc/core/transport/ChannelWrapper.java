package com.simple.rpc.core.transport;

import com.simple.rpc.core.pool.ConnectionObjectFactory;
import io.netty.channel.Channel;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * 包装
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:37
 */
public class ChannelWrapper {

    /**
     * 连接串
     */
    private String connStr;

    /**
     * 主机IP
     */
    private String host;

    /**
     * 端口号
     */
    private int ip;

    private Channel channel;

    private ObjectPool<Channel> channelObjectPool;

    public ChannelWrapper(String host, int port) {

        this.host = host;

        this.ip = port;

        this.connStr = host + ":" + ip;

        channelObjectPool = new GenericObjectPool<Channel>(new ConnectionObjectFactory(host, port));
    }

    public String getConnStr() {

        return connStr;
    }

    public void setConnStr(String connStr) {

        this.connStr = connStr;
    }

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public int getIp() {

        return ip;
    }

    public void setIp(int ip) {

        this.ip = ip;
    }

    public Channel getChannel() {

        return channel;
    }

    public void setChannel(Channel channel) {

        this.channel = channel;
    }

    public ObjectPool<Channel> getChannelObjectPool() {

        return channelObjectPool;
    }

    public void setChannelObjectPool(ObjectPool<Channel> channelObjectPool) {

        this.channelObjectPool = channelObjectPool;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder("ChannelWrapper{");
        sb.append("connStr='").append(connStr).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", ip=").append(ip);
        sb.append(", channel=").append(channel);
        sb.append(", channelObjectPool=").append(channelObjectPool);
        sb.append('}');
        return sb.toString();
    }

    public void close() {
        channelObjectPool.close();
    }
}
