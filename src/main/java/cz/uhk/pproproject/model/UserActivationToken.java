package cz.uhk.pproproject.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class UserActivationToken extends BaseModel{
    public User getUser() {
        return user;
    }

    @OneToOne
    @JoinColumn()
    private User user;
    @Column(nullable = false)
    private String token;

    public UserActivationToken(User user,String token, Date expireDate) {
        this.user = user;
        this.token = token;
        this.expireDate = expireDate;
        this.tokenUsed = false;
    }

    @Column
    private Date expireDate;
    @Column
    private boolean tokenUsed;

    public void setUser(User user) {
        this.user = user;
    }

    public UserActivationToken() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isTokenUsed() {
        return tokenUsed;
    }

    public void setTokenUsed(boolean tokenUsed) {
        this.tokenUsed = tokenUsed;
    }
}
