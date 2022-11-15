package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Email extends BaseModel {
    @Column(nullable = false) @Setter @Getter
    private String subject;
    @Column @Setter @Getter
    private String replyTo;
    @Column(nullable = false) @Setter @Getter
    private String content;
    @OneToOne(targetEntity = User.class) @Setter @Getter
    private User sendTo;

    public Email(String subject, String replyTo, String content, User sendTo) {
        this.subject = subject;
        this.replyTo = replyTo;
        this.content = content;
        this.sendTo = sendTo;
    }


}
