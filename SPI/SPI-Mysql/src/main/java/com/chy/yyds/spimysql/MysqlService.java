package com.chy.yyds.spimysql;


import com.chy.yyds.spi.test.DataStorage;

public class MysqlService implements DataStorage {
    @Override
    public String search(String key) {
        return "服务提供方是mysql";
    }
}
