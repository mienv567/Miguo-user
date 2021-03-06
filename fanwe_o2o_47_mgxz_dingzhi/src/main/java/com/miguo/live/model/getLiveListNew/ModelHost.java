package com.miguo.live.model.getLiveListNew;

import java.util.List;

/**
 * 主播
 * Created by Administrator on 2016/7/27.
 */
public class ModelHost {
//        "host": {
//            "uid": "9d861276-8ddd-4d47-83f0-512dcc5efdcb",                              主播id
//            "host_user_id": "9d861276-8ddd-4d47-83f0-512dcc5efdcb",
//                    "nickname": "19999999996",                                                  主播昵称
//            "avatar": "",                                                                主播头像
//            "tags": ["可爱",
//                    "意识流",																  主播的标签
//            "腿长一米八",
//                    "小吃搜街",
//                    "试睡"]                                                                  主播的标签
//        },

    private String uid;

    private String nickname;

    private List<String> tags;
    private String avatar;
    /**
     * 主播用户id. userid
     */
    private String host_user_id;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRealNickName(){
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getHost_user_id() {
        return host_user_id;
    }

    public void setHost_user_id(String host_user_id) {
        this.host_user_id = host_user_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
