package kz.ilotterytea.bot.models;

/**
 * User (chatter) model.
 * @author ilotterytea
 * @since 1.0
 */
public class UserModel {
    private final String aliasId;
    private boolean isSuperuser;

    public UserModel(
            String aliasId,
            boolean isSuperuser
    ) {
        this.aliasId = aliasId;
        this.isSuperuser = isSuperuser;
    }

    public String getAliasId() { return aliasId; }
    public boolean isSuperUser() { return isSuperuser; }
    public void setSuperuser(boolean isSuperuser) { this.isSuperuser = isSuperuser; }
}
