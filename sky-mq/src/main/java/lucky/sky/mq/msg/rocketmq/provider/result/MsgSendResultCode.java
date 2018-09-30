package lucky.sky.mq.msg.rocketmq.provider.result;


public enum MsgSendResultCode {
    /** 消息发送成功 */
    SEND_SUCCESS,
    /** 消息发送失败 */
    SEND_FAILED,
    /** 序列化失败 */
    SERIALIZABLE_FAILED;

}
