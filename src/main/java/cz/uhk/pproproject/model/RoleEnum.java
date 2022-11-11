package cz.uhk.pproproject.model;

public enum RoleEnum {
    EMPLOYEE,MANAGER,OWNER,ADMIN;
    public String getRoleWithPrefix(RoleEnum role){
        return "ROLE_"+role;
    }
}
