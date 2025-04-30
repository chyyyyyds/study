package com.chy.yyds.spi.test;

public class MysqlStorage implements DataStorage {
    @Override
    public String search(String key) {
        return "【Mysql】搜索" + key + "，结果：No";
    }
}

