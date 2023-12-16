package dgs.reminder.dgs_reg_remind.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:auth.properties")
public class PlayerService {

    //Many of these properties will be moved to a "Player Object" once a database is set up for each person. For now, we can keep in auth.properties

    @Value("${player.email}")
    private String email;

    @Value("${player.password}")
    private String password;

    @Value("${player.name}")
    private String name;

    @Value("${player.division}")
    private String division;

    @Value("${player.pdgaNumber}")
    private String pdgaNumber;

    @Value("${player.phone}")
    private String phone;

    @Value("${player.city}")
    private String city;

    @Value("${player.state}")
    private String state;

    @Value("${player.country}")
    private String country;


    public WebDriver login(WebDriver driver){
        try {
            driver.findElement(By.id("login-u")).sendKeys(email);
            driver.findElement(By.id("login-p")).sendKeys(password);
            driver.findElement(By.className("login")).submit();
        } catch (Exception e){
            System.out.println("Unable to log in user");
        }
        return driver;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getDivision() {
        return division;
    }

    public String getPdgaNumber() {
        return pdgaNumber;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }
}
