package com.hayaizo.chatsystem.adapter;


import com.hayaizo.chatsystem.common.Enum.WSRespTypeEnum;
import com.hayaizo.chatsystem.dto.response.WSBaseResp;
import com.hayaizo.chatsystem.dto.response.WSLoginSuccessResp;
import com.hayaizo.chatsystem.po.User;
import com.hayaizo.chatsystem.po.UserProfile;
import org.springframework.stereotype.Component;
/**
 * ws消息适配器
 */
@Component
public class WSAdapter {


    /**
     * 登陆成功
     */
    public static WSBaseResp<WSLoginSuccessResp> buildLoginSuccessResp(User user, UserProfile userProfile, String token, byte isAdmin) {
        WSBaseResp<WSLoginSuccessResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccessResp wsLoginSuccess = WSLoginSuccessResp.builder()
                .avatar(userProfile.getAvatarUrl())
                .name(user.getUsername())
                .token(token)
                .uid(userProfile.getUserId())
                .power(isAdmin)
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }
}
