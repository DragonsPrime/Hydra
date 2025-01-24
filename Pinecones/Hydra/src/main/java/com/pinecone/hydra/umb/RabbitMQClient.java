package com.pinecone.hydra.umb;

import com.pinecone.hydra.system.Hydrarum;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQClient implements Pinenut {
    protected Hydrarum       mSystem;
    protected JSONObject     mjoSectionConf;
    protected String         mszHost;
    protected short          mnPort;
    protected int            mnKeepAliveTimeout;
    protected int            mnSocketTimeout;
    protected String         mszUsername;
    protected String         mszPassword;
    protected String         mszVHost = "/wolf";

    public RabbitMQClient(Hydrarum system, JSONObject joConf ) {
        this.mSystem        = system;
        this.mjoSectionConf = joConf;

        this.apply( joConf );
    }

    public RabbitMQClient apply( JSONObject joConf ) {
        this.mjoSectionConf = joConf;

        this.mszHost            = this.mjoSectionConf.optString( "host" );
        this.mnPort             = (short) this.mjoSectionConf.optInt( "port" );
        this.mszPassword        = this.mjoSectionConf.optString( "password" );
        this.mszUsername        = this.mjoSectionConf.optString( "username" );

        return this;
    }

    public void toListen() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost( this.mszHost );
        connectionFactory.setPort( this.mnPort  );
        connectionFactory.setUsername( this.mszUsername );
        connectionFactory.setPassword( this.mszPassword );
        connectionFactory.setVirtualHost( this.mszVHost );



        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("fuck", false, false, false, null);

        for ( int i = 0; i < 1e3; i++ ) {
            channel.basicPublish("", "fuck", null, "Jesus fucking christ".getBytes());
        }

        channel.close();
        connection.close();
    }

    @Override
    public String toString() {
        return String.format(
                "[object %s(0x%s)<\uD83D\uDC07>]",
                this.className() , Integer.toHexString( this.hashCode() )
        );
    }

    @Override
    public String toJSONString() {
        return "\"" + this.toString() + "\"";
    }
}
