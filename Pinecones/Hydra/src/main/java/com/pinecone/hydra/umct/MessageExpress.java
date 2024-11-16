package com.pinecone.hydra.umct;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.express.Express;

/**
 *  Pinecone Ursus For Java UMCT Message Express [ Uniform Message Control Transmit ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Transmit Integrated Model - Express
 *  统一消息控制与数据传输一体化模型 - 总线分发调度器
 *  **********************************************************
 */

public interface MessageExpress extends Express {

    String getName();

    ArchMessagram getMessagram();

    MessageDeliver  recruit ( String szName );

    MessageExpress  register( Deliver deliver );

    MessageExpress  fired   ( Deliver deliver );



}
