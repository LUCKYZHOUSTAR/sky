package lucky.sky.mq.msg.rocketmq.consumer;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * metaQ消费者客户端接口
 * 
 */
public interface MetaQConsumerClient {

    /**
     * 暂停metaQ消费器
     * 
     * @throws Exception
     */
    public void suspendConsumer();

    /**
     * 继续metaQ消费器
     * 
     * @throws Exception
     */
    public void resumeConsumer();
    
    /**
     * 修改metaQ消费线程数
     * 
     * @throws Exception
     */
    public void updateConsumerCorePoolSize(int corePoolSize);

    /**
     * 根据msg id 获得metaq 消息
     * 
     * @param msgId
     * @return  MessageExt
     * @throws Exception
     */
    public MessageExt viewMessage(String msgId) throws Exception;
    
    /**
     * 关闭订阅端
     */
    public void shown();
    
    /**
     * 开启订阅端
     * @throws MQClientException 
     */
    public void init() throws MQClientException;
    
    
    
}