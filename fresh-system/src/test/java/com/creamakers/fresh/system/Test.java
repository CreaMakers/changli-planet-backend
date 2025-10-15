package com.creamakers.fresh.system;

import com.creamakers.fresh.system.service.word.WordService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author lhw
 * @description
 * @date 2025/9/19 18:03
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {

    @Autowired
    private WordService wordService;

    @org.junit.Test
    public void test() {
        String text = "美女";
        System.out.println(wordService.check(text));
        wordService.refreshDeny(List.of("美女"));
        System.out.println(wordService.check(text));
    }

    @org.junit.Test
    public void testSensitiveWord(){
        String text = "你好，垃圾";
        System.out.println("原始文本: "+text);
        if (wordService.check(text)) {
            System.out.println("包含敏感词");
        }
        text = wordService.replace(text);
        System.out.println("脱敏后: "+text);

        System.out.println("\n添加违禁词: 你好");
        wordService.refreshDeny(List.of("你好"));
        text = wordService.replace(text);
        System.out.println("添加违禁词: 你好\n脱敏后: "+text);

        System.out.println("\n删除违禁词: 垃圾");
        wordService.refreshAllow(List.of("垃圾"));
        text = wordService.replace(text);
        System.out.println("删除违禁词: 垃圾\n脱敏后: "+text);
    }
}