package pojos;

public class TeacherWithAccountInfo extends Teacher {
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long hasAccount) {
        this.accountId = hasAccount;
    }
}
