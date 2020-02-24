package life.majiang.community.model;

public class Notification {
    private Long id;


    private Long notifier;


    private Long receiver;


    /**
     * 用于标识该通知的源头
     * 如果该通知来自某一用户对当前用户发布的问题的评论，则该outerid为该问题的id
     * 如果该通知来自某一用户对当前用户的评论的回复，则该outerid为该回复的id
     */
    private Long outerid;


    //用于区分是源头是问题还是评论，以应对以后可能的不同处理
    private Integer type;

    private Long gmtCreate;

    //用于标识该通知的状态已读(1)/未读(0)，默认未读(0)
    private Integer status;


    //该通知在页面展示时通知来源用户的用户名
    private String notifierName;


    //该通知在页面展示时所显示的标题
    private String outerTitle;


    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.id
     *
     * @param id the value for notification.id
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.notifier
     *
     * @return the value of notification.notifier
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public Long getNotifier() {
        return notifier;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.notifier
     *
     * @param notifier the value for notification.notifier
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setNotifier(Long notifier) {
        this.notifier = notifier;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.receiver
     *
     * @return the value of notification.receiver
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public Long getReceiver() {
        return receiver;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.receiver
     *
     * @param receiver the value for notification.receiver
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.outerId
     *
     * @return the value of notification.outerId
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public Long getOuterid() {
        return outerid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.outerId
     *
     * @param outerid the value for notification.outerId
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setOuterid(Long outerid) {
        this.outerid = outerid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.type
     *
     * @return the value of notification.type
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.type
     *
     * @param type the value for notification.type
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.gmt_create
     *
     * @return the value of notification.gmt_create
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public Long getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.gmt_create
     *
     * @param gmtCreate the value for notification.gmt_create
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.status
     *
     * @return the value of notification.status
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.status
     *
     * @param status the value for notification.status
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.NOTIFIER_NAME
     *
     * @return the value of notification.NOTIFIER_NAME
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public String getNotifierName() {
        return notifierName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.NOTIFIER_NAME
     *
     * @param notifierName the value for notification.NOTIFIER_NAME
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setNotifierName(String notifierName) {
        this.notifierName = notifierName == null ? null : notifierName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column notification.OUTER_TITLE
     *
     * @return the value of notification.OUTER_TITLE
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public String getOuterTitle() {
        return outerTitle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column notification.OUTER_TITLE
     *
     * @param outerTitle the value for notification.OUTER_TITLE
     *
     * @mbg.generated Mon Feb 24 00:57:59 CST 2020
     */
    public void setOuterTitle(String outerTitle) {
        this.outerTitle = outerTitle == null ? null : outerTitle.trim();
    }
}