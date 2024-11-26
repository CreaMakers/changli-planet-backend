package com.hayaizo.chatsystem.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hayaizo.chatsystem.mapper.UserProfileMapper;
import com.hayaizo.chatsystem.po.UserProfile;
import com.hayaizo.chatsystem.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class UserProfileImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {


}
