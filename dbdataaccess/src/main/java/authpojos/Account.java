package authpojos;

import java.sql.Date;

public class Account {
    private String accountId;
    private String userLogin;
    private String roleName;
    private String passwordHash;
    private Date lastLoginTs;
    private Date registerTs;

    public String getAccountId() {
        return accountId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getRoleName() {
        return roleName;
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
