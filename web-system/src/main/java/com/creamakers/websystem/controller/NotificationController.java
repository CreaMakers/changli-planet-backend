package com.creamakers.websystem.controller;

import com.creamakers.websystem.dao.NotificationMapper;
import com.creamakers.websystem.domain.dto.Notification;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.NotificationReq;
import com.creamakers.websystem.domain.vo.response.NotificationResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/web")
public class NotificationController {
    @Autowired
    private NotificationMapper notificationMapper;

    @PostMapping("/notifications/system")
    public ResultVo<NotificationResp> addNotification(@RequestBody NotificationReq notificationReq) {
        Notification notification = new Notification();
        notification.setContent(notificationReq.getContent())
                .setDescription(notificationReq.getDescription())
                .setSenderId(notificationReq.getSenderId())
                .setNotificationType(1)
                .setSendTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification,notificationResp);
        return null;
    }
}
