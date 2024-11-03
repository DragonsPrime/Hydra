package com.sauron.shadow.chronicle;

import com.pinecone.framework.system.homotype.StereotypicInjector;
import com.pinecone.framework.util.json.JSONObject;
import com.sauron.radium.heistron.chronic.Raider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *  Bean Nuts Hazelnut Sauron Radium - Sauron`s Shadow For Java, Clerk [史官, 书记]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Cooperate with the chronicle system for periodic crawler to retrieve data.
 *  配合编年史系统，面向周期性爬虫数据取回
 *  *****************************************************************************************
 *  Dragon King, the undefined
 */
public interface Clerk extends Raider {
    JSONObject getConfig();

    default StereotypicInjector autoInject( Class<?> stereotype ) {
        return this.getSystem().getPrimaryConfigScope().autoInject(
                stereotype, this.getConfig(), this
        );
    }

    default String nowDateTime() {
        return LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ) );
    }
}
