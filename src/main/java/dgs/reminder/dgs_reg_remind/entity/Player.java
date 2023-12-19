package dgs.reminder.dgs_reg_remind.entity;


public class Player {

    private String uuid;
    private String email;
    private String password;
    private String name;
    private String division;
    private String pdgaNumber;
    private String phone;
    private String city;
    private String state;
    private String country;

    public String getUuid(){
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPdgaNumber() {
        return pdgaNumber;
    }

    public void setPdgaNumber(String pdgaNumber) {
        this.pdgaNumber = pdgaNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
