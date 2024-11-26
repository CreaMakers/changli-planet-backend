package com.hayaizo.chatsystem.service.Impl;

import cn.hutool.core.collection.CollectionUtil;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hayaizo.chatsystem.adapter.WSAdapter;
import com.hayaizo.chatsystem.dto.request.WSChannelExtraDTO;
import com.hayaizo.chatsystem.dto.response.WSBaseResp;
import com.hayaizo.chatsystem.mapper.UserMapper;
import com.hayaizo.chatsystem.mapper.UserProfileMapper;
import com.hayaizo.chatsystem.po.User;
import com.hayaizo.chatsystem.po.UserProfile;
import com.hayaizo.chatsystem.service.WebSocketService;
import com.hayaizo.chatsystem.utils.JwtUtil;
import com.hayaizo.chatsystem.websocket.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * websocket处理类
 */
@Component
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * 所有请求登录的code与channel关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();

    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String TOKEN_PREFIX;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;


    public static ConcurrentHashMap<Channel, WSChannelExtraDTO> getOnlineMap() {
        return ONLINE_WS_MAP;
    }



    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel,new WSChannelExtraDTO());
    }


    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Integer> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offline = offline(channel, uidOptional);
        if(offline){
            // 用户全都下线了
            // TODO Redis中缓存的删除
        }
    }

    @Override
    public void authorize(Channel channel, String token) {
        // Token校验
        boolean verify = jwtUtil.verify(token);
        if(verify){
            // 去数据库查询用户信息，返回给前端
            String username = jwtUtil.getUserNameFromToken(token);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                    .eq(User::getUsername, username)
                    .eq(User::getIsDeleted, 0);
            User user = userMapper.selectOne(queryWrapper);
            Integer userId = user.getUserId();
            LambdaQueryWrapper<UserProfile> profileLambdaQueryWrapper = Wrappers.lambdaQuery(UserProfile.class)
                    .eq(UserProfile::getUserId, userId);
            UserProfile userProfile = userProfileMapper.selectOne(profileLambdaQueryWrapper);
            loginSuccess(channel,user,userProfile,token);
        }else{
            // 让前端把Token失效
        }

    }

    /**
     * 发给所有人，跳过自己
     */
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel,ext)->{
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            // TODO 后续可以改成通用线程池
            sendMsg(channel,wsBaseResp);
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        sendToAllOnline(wsBaseResp, null);
    }

    private void loginSuccess(Channel channel,User user,UserProfile userProfile, String token){
        // 更新在线列表
        online(channel,userProfile.getUserId());
        // 判断一下权限
        Byte isAdmin = user.getIsAdmin();
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user,userProfile,token,isAdmin));
        // TODO 后面会存Redis缓存
    }

    /**
     * 用户上线
     */
    public void online(Channel channel, Integer uid) {
        // 先更新channel和uid的关系
        getOrInitChannelExt(channel).setUid(uid);
        // 再更新uid和channel的关系
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    /**
     * 如果在线列表不存在，就先把该channel放进在线列表
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    /**
     * 处理用户下线逻辑，从全局映射中移除对应的连接和用户 ID 的关系。
     *
     * @param channel 当前用户的连接通道
     * @param uidOptional 可选的用户 ID（如果存在则处理用户 ID 的映射）
     * @return 返回 true 表示用户已完全下线（没有任何活动连接），false 表示用户仍有其他活跃连接
     */
    private boolean offline(io.netty.channel.Channel channel, Optional<Integer> uidOptional) {
        // 1. 从在线连接映射中移除当前的通道
        ONLINE_WS_MAP.remove(channel);

        // 2. 如果用户 ID 存在，处理用户 ID 与通道的映射
        if (uidOptional.isPresent()) {
            // 通过用户 ID 获取当前用户的所有活跃连接列表
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());

            // 检查用户的连接列表是否为空
            if (CollectionUtil.isNotEmpty(channels)) {
                // 从连接列表中移除当前通道
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }

            // 检查用户的连接列表是否已经清空
            // 如果清空，表示用户完全下线，返回 true
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }

        // 3. 如果用户 ID 不存在，默认返回 true，认为用户完全下线
        return true;
    }

    /**
     * 给本地channel发送消息
     *
     * @param channel
     * @param wsBaseResp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

}
