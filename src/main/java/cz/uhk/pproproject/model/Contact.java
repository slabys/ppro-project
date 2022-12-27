package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class Contact extends BaseModel {
    @Getter @Setter @Column
    private String city;
    @Getter @Setter @Column
    private String state;
    @Getter @Setter @Column
    private String street;
    @Getter @Setter @Column
    private Integer zipCode;
    @Getter @Setter @Column
    private String phone;
    @Getter @Setter @Column
    private String bankAccount;

    public String getFullAddress() {
        return street + ", " + city + ", " + zipCode + ", " + state;
    }

    public void setEmptyValuesToNull() {
        if (this.getCity().trim().equals("")) this.setCity(null);
        if (this.getStreet().trim().equals("")) this.setStreet(null);
        if (this.getState().trim().equals("")) this.setState(null);
        if (this.getPhone().trim().equals("")) this.setPhone(null);
        if (this.getBankAccount().trim().equals("")) this.setBankAccount(null);
    }

    public boolean isAddressUnset() {
        return this.getCity() == null && this.getStreet() == null &&
                this.getState() == null && this.getZipCode() == null;
    }

    public boolean isContactUnset() {
        return isAddressUnset() && this.getPhone() == null && this.getBankAccount() == null;
    }
}
