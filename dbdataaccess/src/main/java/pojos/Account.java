package pojos;

import java.sql.Date;

public class Account {
    private String accountId;
    private String userLogin;
    private String userRole;
    private String passwordHash;
    private Date lastLoginTs;
    private Date registerTs;

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setLastLoginTs(Date lastLoginTs) {
        this.lastLoginTs = lastLoginTs;
    }

    public void setRegisterTs(Date registerTs) {
        this.registerTs = registerTs;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Date getLastLoginTs() {
        return lastLoginTs;
    }

    public Date getRegisterTs() {
        return registerTs;
    }
}
