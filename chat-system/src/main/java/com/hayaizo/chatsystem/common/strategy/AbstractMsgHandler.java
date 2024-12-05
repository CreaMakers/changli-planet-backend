package com.hayaizo.chatsystem.common.strategy;

import cn.hutool.core.bean.BeanUtil;
import com.hayaizo.chatsystem.common.Enum.MessageTypeEnum;
import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import com.hayaizo.chatsystem.service.ChatGroupMessageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;

/**
 * 消息处理器顶层抽象
 */
@Component
public abstract class AbstractMsgHandler<Req> {

    @Autowired
    private ChatGroupMessageService chatGroupMessageService;

    private Class<Req> bodyClass;

    @PostConstruct
    private void init(){
        // 获取当前类的直接父类的泛型信息（如 BaseHandler<TextRequest>）
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();

        // 获取第一个泛型参数的类型（即 Req 的实际类型）
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];

        // 将当前处理器注册到消息处理工厂，以支持消息类型的动态分发
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    @Transactional
    public Integer checkAndSaveMsg(ChatMessageReq request, Integer uid) {
        Req body = this.toBean(request.getBody());
        // TODO 校验参数，判断是否满足注解的限制
        // 检验消息是否是艾特xxx（包括批量艾特）或者回复某条消息
        checkMsg(body, request.getRoomId(), uid);
        // 构建消息实体类
        ChatGroupMessage insert = ChatGroupMessage.builder()
                .senderId(uid) // 设置发送者的用户ID
                .groupId(request.getRoomId()) // 设置聊天房间ID
                .fileType(request.getMsgType()) // 设置消息类型
                .isDeleted(0) // 设置消息状态为正常
                .build();
        // 统一保存
        chatGroupMessageService.save(insert);
        // TODO 扩展字段的保存
        saveMsg(insert,body);
        return insert.getMessageId();
    }

    /**
     * 消息类型
     */
    protected abstract MessageTypeEnum getMsgTypeEnum();

    protected void checkMsg(Req body, Integer roomId, Integer uid) {

    }

    private Req toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (Req) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }

    protected abstract void saveMsg(ChatGroupMessage message, Req body);

    /**
     * 展示消息
     */
    public abstract Object showMsg(ChatGroupMessage msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(ChatGroupMessage msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(ChatGroupMessage msg);


}
