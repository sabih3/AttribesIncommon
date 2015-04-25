package com.attribes.incommon.api;

/**
 * Created with IntelliJ IDEA.
 * User: ashoaib
 * Date: 5/30/13
 * Time: 10:37 PM
 * This file has been made to define all the constants associated with the webservice package.
 */
public enum WebserviceElement {
    AUTH("auth"),
    XCSRF_TOKEN("X-CSRFToken"),
    WEB_COOKIE_XCSRF_TOKEN("csrftoken"),
    COLLABORATION_UID("collaboration_uid"),
    HASHTAG("hashtags"),
    TAG("tag"),
    FRIEND_INVITATION("friend_invitation"),
    COLLABORATION_INVITATION("collaboration_invitation"),
    PASSWORD_CHANGE("password_change"),
    CREATOR_UID("creator_uid"),
    DOCUMENT_MENTION("document_mention"),
    ACCEPTED("accepted"),
    APP_SETTINGS("app_settings"),
    PENDING("pending"),
    POST_LIMIT("limit"),
    POST_UID("post_uid"),
    PARENT_POST_UID("parent_post_uid"),
    MARK_AS_READ("markAsRead"),
    POST_FILTER_ACTIVE("filter_active"),
    THREAD_UPDATED_LTE("thread_updated_lte"),
    THREAD_UPDATED_LT_UID("thread_updated_lt_uid"),
    UPDATED_LTE("updated_lte"),
    UPDATED_LT_UID("updated_lt_uid"),
    THREAD_UPDATED_GTE("thread_updated_gte"),
    THREAD_UPDATED_GT_UID("thread_updated_gt_uid"),
    CREATOR_LTE("created_lte"),
    CREATOR_GTE("created_gte"),
    LATEST("latest"),
    GENERATED("generated"),
    CREATOR_LTE_UID("created_lt_uid"),
    CREATOR_GTE_UID("created_gt_uid"),
    COMMENTS_LIMIT("comments_limit"),
    USER_LIKE_LIMIT("users_liked_limit"),
    SERIAL("serial"),
    DOCUMENT_UID("document_uid"),
    DEVICE_UID("device_uid"),
    UID("uid"),
    DELETED("deleted"),
    COLLABORATION_FILTER("collaboration_filter"),
    COLLABORATIONS("collaborations"),
    COLLABORATION_GROUPS("collaboration_groups"),
    FOLDERS("folders"),
    DOCUMENTS("documents"),
    DOCUMENT_VERSIONS("document_versions"),
    DOCUMENT_LOCKS("document_locks"),
    COLLABORATION_MEMBERSHIPS("collaboration_memberships"),
    COLLABORATION_PERMISSIONS("collaboration_permissions"),
    DEFAULT_DOCUMENT_PERMISSIONS("default_document_permissions"),
    DEFAULT_COLLABORATION_PERMISSIONS("default_collaboration_permissions"),
    DOCUMENT_PERMISSIONS("document_permissions"),
    CONTACTS("contacts"),
    FRIENDS("friends"),
    USERS("users"),
    STATUS("status"),
    KABUTO_RESPONSE_STATUS("status"),
    KABUTO_RESPONSE_STATUS_CODE("code"),
    KABUTO_RESPONSE_STATUS_DETAILS("details"),
    HTTP_SOCKET_TIMEOUT("http.socket.timeout"),
    HTTP_CONNECTION_MANAGER_TIMEOUT("http.connection-manager.timeout"),
    COLLABORATORS("collaborators"),
    EMAIL_ADDRESSES("email_addresses"),
    GET_USER_ATTRIBUTES("get_user_attributes"),
    POSTS("posts"),
    GET_ATTRIBUTES("attributes"),
    OP("op"),
    DISPOSITION("disposition"),
    TOKEN("token"),
    PASSWORD("password"),
    INVITATIONS("invitations"),
    UIDS("uids"),
    PIVOT_ID("pivot_id"),
    AFTER("after"),
    NEWEST_PIVOT_ID("newest_pivot_id"),
    VALUES("values"),
    RESULTS("results");

    private String keyName;

    private WebserviceElement(String name) {
        this.keyName = name;
    }

    public String getKeyName() {
        return keyName;
    }


}
