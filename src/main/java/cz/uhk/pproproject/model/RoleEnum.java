package cz.uhk.pproproject.model;

import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

public enum RoleEnum {
    EMPLOYEE,MANAGER,OWNER,ADMIN;
    public String getRoleWithPrefix(RoleEnum role){
        return "ROLE_"+role;
    }

    public static RoleHierarchyImpl getRoleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_OWNER \n ROLE_OWNER > ROLE_MANAGER \n ROLE_MANAGER > ROLE_EMPLOYEE");
        return roleHierarchy;
    }
}
