package dgs.reminder.dgs_reg_remind.entity;

public class PlayerCredential {

    private String playerUUID;
    private String username;
    private String password;

    public PlayerCredential(String playerUUID, String username, String password){
        this.playerUUID = playerUUID;
        this.username = username;
        this.password = password;
    }


}
