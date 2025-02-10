package com.hayaizo.chatsystem.service;


import com.hayaizo.chatsystem.common.constant.MQConstant;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.WSBaseResp;
import io.netty.channel.Channel;

import java.util.List;

public interface WebSocketService {

    /**
     * 处理所有ws连接的事件
     *
     * @param channel
     */
    void connect(Channel channel);

    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    void removed(Channel channel);

    /**
     * 主动认证登录
     *
     * @param channel
     * @param token
     */
    void authorize(Channel channel, String token);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp);

    void sendToUid(WSBaseResp<?> wsBaseResp, Integer uid);

    void sendToUids(WSBaseResp<?> wsBaseResp, List<Integer> uids);


//    void sendToUid(WSBaseResp<?> wsBaseResp, Long uid);

}
