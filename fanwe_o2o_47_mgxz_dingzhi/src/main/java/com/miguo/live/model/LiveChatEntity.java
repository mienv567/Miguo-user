package com.miguo.live.model;

/**
 * Created by didik on 2016/7/26.
 */
public class LiveChatEntity {
    private String grpSendName;
    private String content;
    private int  type;
    /**
     * 用户 头像。
     */
    private String faceUrl;

    public LiveChatEntity() {
        // TODO Auto-generated constructor stub
    }



    public String getSenderName() {
        return grpSendName;
    }

    public void setSenderName(String grpSendName) {
        this.grpSendName = grpSendName;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String context) {
        this.content = context;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGrpSendName() {
        return grpSendName;
    }

    public void setGrpSendName(String grpSendName) {
        this.grpSendName = grpSendName;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }
}
