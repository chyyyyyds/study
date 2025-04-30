package com.chy.yyds.spi.demo;

import com.chy.yyds.spi.test.DataStorage;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SpiDemo {

    public static void main(String[] args) {
        ServiceLoader<DataStorage> serviceLoader = ServiceLoader.load(DataStorage.class);
        System.out.println("============ Java SPI 测试============");
        Iterator<DataStorage> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            DataStorage loader = iterator.next();
            System.out.println(loader.search("Yes Or No"));
        }

    }

}

