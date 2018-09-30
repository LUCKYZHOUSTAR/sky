/**
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package lucky.sky.mq.msg.rocketmq.provider.result;


/**
 * 消息处理结果类
 * <pre>
 * 包含序列化、发送两个环节
 * </pre>
 */
public class MsgSendResult {
    /** 是否发送成功  */
    private boolean sendSuccess;

    /** 结果码，包含发送成功，序列化失败，发送失败等值 */
    private String  resultCode;
    
    /** 结果信息 */
    private String  resultmsg;

    /** 若发送成功返回messageId */
    private String  messageId;

    /**
     * genSuccessResult
     * @param messageId
     * @return
     */
    public static MsgSendResult genSuccessResult(String messageId) {
        MsgSendResult successResult = new MsgSendResult(true,
            MsgSendResultCode.SEND_SUCCESS.name(), messageId,null);
        return successResult;
    }

    /**
     * genFailedResult
     * @return
     */
    public static MsgSendResult genFailedResult(String resultmsg) {
        MsgSendResult failedResult = new MsgSendResult(false, MsgSendResultCode.SEND_FAILED.name(), null,resultmsg);
        return failedResult;
    }

    @Override
    public String toString() {
        return "MsgSendResult [sendSuccess=" + sendSuccess + ", resultCode=" + resultCode
               + ", resultmsg=" + resultmsg + ", messageId=" + messageId + "]";
    }
    

     /* @param sendSuccess
     * @param resultCode
     * @param messageId
     */
    private MsgSendResult(boolean sendSuccess, String resultCode, String messageId,String resultmsg) {
        super();
        this.sendSuccess = sendSuccess;
        this.resultCode = resultCode;
        this.messageId = messageId;
        this.resultmsg = resultmsg;
    }
   


    /**
     * isSendSuccess
     * @return
     */
    public boolean isSendSuccess() {
        return sendSuccess;
    }

    /**
     * getResultCode
     * @return
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * getMessageId
     * @return
     */
    public String getMessageId() {
        return messageId;
    }

    public String getResultmsg() {
        return resultmsg;
    }

    public void setResultmsg(String resultmsg) {
        this.resultmsg = resultmsg;
    }

}
