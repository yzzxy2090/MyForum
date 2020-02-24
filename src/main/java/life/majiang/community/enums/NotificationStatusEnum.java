package life.majiang.community.enums;

public enum NotificationStatusEnum {
    //未读(0)，数据库默认新通知为未读
    UNREAD(0),
    //已读(1)
    READ(1);


    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
