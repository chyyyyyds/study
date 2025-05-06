package com.chy.yyds.spiredis;


import com.chy.yyds.spi.test.DataStorage;

public class RedisServer implements DataStorage {

    @Override
    public String search(String key) {
        return "服务提供方是redis";
    }

}
