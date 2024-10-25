package com.creamakers.websystem.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.util.DigestUtils;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CaffeineUtils {

    public static void main(String[] args) {
        String password1 = new String(DigestUtils.md5DigestAsHex("123456".getBytes()));
        String password2 = new String(DigestUtils.md5DigestAsHex("111111".getBytes()));
        System.out.println(password1);
        System.out.println(password2);
    }
}
