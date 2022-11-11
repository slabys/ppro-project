package cz.uhk.pproproject.model;

import javax.persistence.*;

@Entity
public class Email extends BaseModel {
    @Column(nullable = false)
    private String subject;
    @Column
    private String replyTo;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @ManyToOne(targetEntity = User.class)
    private String sendTo;

    public Email(String subject, String replyTo, String content, String sendTo) {
        this.subject = subject;
        this.replyTo = replyTo;
        this.content = content;
        this.sendTo = sendTo;
    }

    public Email() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
}
