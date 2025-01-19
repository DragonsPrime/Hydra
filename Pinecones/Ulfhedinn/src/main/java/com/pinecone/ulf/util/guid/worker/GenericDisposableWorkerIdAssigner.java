package com.pinecone.ulf.util.guid.worker;


import com.pinecone.ulf.util.guid.utils.DockerUtils;
import com.pinecone.ulf.util.guid.utils.NetUtils;
import com.pinecone.ulf.util.guid.worker.entity.WorkerNodeEntity;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericDisposableWorkerIdAssigner implements WorkerIdAssigner {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDisposableWorkerIdAssigner.class);

    // 使用 ConcurrentHashMap 保存已分配的 worker ID，键是 worker ID，值是 WorkerNodeEntity
    private static final ConcurrentHashMap<Long, WorkerNodeEntity > WORKER_NODES = new ConcurrentHashMap<>();

    // 使用 AtomicInteger 作为 worker ID 的分配器
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    /**
     * 基于内存中的列表分配 worker ID。
     *
     * @return 分配的 worker ID
     */
    @Override
    public long assignWorkerId() {
        // 构建 worker 节点实体
        // todo 要将实体类确定下来，再根据其他逻辑生成wordId，目前为测试版本
        WorkerNodeEntity workerNodeEntity = this.buildWorkerNode();

        // 从 NEXT_ID 获取下一个可用的 worker ID
        int id = NEXT_ID.getAndIncrement();

        // 将 worker ID 和对应的 WorkerNodeEntity 存入内存中的列表
        WORKER_NODES.put((long) id, workerNodeEntity);

        // 使用 ThreadLocalRandom 生成介于 0（包括）和 1000（不包括）之间的随机整数
        return ThreadLocalRandom.current().nextInt( 0, 1000 );
    }

    /**
     * 根据 IP 和端口构建 worker 节点实体
     */
    private WorkerNodeEntity buildWorkerNode() {
        WorkerNodeEntity workerNodeEntity = new WorkerNodeEntity();
        if ( DockerUtils.isDocker() ) {
            workerNodeEntity.setType(WorkerNodeType.CONTAINER.value());
            workerNodeEntity.setHostName(DockerUtils.getDockerHost());
            workerNodeEntity.setPort(DockerUtils.getDockerPort());

        }
        else {
            workerNodeEntity.setType(WorkerNodeType.ACTUAL.value());
            workerNodeEntity.setHostName(NetUtils.getLocalAddress());
            workerNodeEntity.setPort(System.currentTimeMillis() + "-" + RandomUtils.nextInt(100000));
        }

        return workerNodeEntity;
    }
}
