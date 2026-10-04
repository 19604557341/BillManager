package com.example.billmanager.vo.amount;

import lombok.Data;

@Data
public class FieldCheckVO {

    private boolean available;

    private String message;

    public static FieldCheckVO ok(String message) {
        FieldCheckVO vo = new FieldCheckVO();
        vo.setAvailable(true);
        vo.setMessage(message);
        return vo;
    }

    public static FieldCheckVO fail(String message) {
        FieldCheckVO vo = new FieldCheckVO();
        vo.setAvailable(false);
        vo.setMessage(message);
        return vo;
    }
}
