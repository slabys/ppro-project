package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class Contact extends BaseModel{
    @Getter @Setter @Column
    private String city;
    @Getter @Setter @Column
    private String state;
    @Getter @Setter @Column
    private String street;
    @Getter @Setter @Column
    private String zipCode;
    @Getter @Setter @Column
    private String phone;
    @Getter @Setter @Column
    private String bankAccount;

    public String getFullAddress(){
        return street + ", " + city + ", " + zipCode + ", " + state;
    }
}
