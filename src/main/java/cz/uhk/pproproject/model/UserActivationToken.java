package cz.uhk.pproproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
public class UserActivationToken extends BaseModel{

    @OneToOne(targetEntity = User.class) @Setter @Getter
    private User user;

    @Column(nullable = false) @Setter @Getter
    private String token;

    public UserActivationToken(User user,String token, Date expireDate) {
        this.user = user;
        this.token = token;
        this.expireDate = expireDate;
        this.tokenUsed = false;
    }

    @Column @Setter @Getter
    private Date expireDate;
    @Column @Setter @Getter
    private boolean tokenUsed;

}
