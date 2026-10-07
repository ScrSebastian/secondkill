package com.xxxx.secondkill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
    private String requestId;
    private Long userId;
    private Long goodsId;
}
