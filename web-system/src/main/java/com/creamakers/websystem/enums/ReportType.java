package com.creamakers.websystem.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 举报类型枚举
 */
@Getter
public enum ReportType {
    涉政有害("涉政有害"),
    不友善("不友善"),
    垃圾广告("垃圾广告"),
    违法违规("违法违规"),
    色情低俗("色情低俗"),
    涉嫌侵权("涉嫌侵权"),
    网络暴力("网络暴力"),
    抄袭("抄袭"),
    自杀自残("自杀自残"),
    不实信息("不实信息"),
    其他("其他");

    @EnumValue  // 用于 MyBatis-Plus 存储时使用
    @JsonValue  // 用于 JSON 序列化
    private final String value;

    ReportType(String value) {
        this.value = value;
    }

    @JsonCreator  // 用于反序列化时匹配
    public static ReportType fromValue(String value) {
        for (ReportType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的举报类型: " + value);
    }
}
