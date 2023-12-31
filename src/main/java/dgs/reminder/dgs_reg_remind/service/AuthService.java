package dgs.reminder.dgs_reg_remind.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:auth.properties")

public class AuthService {

    @Value("${player.email}")
    private String email;

    @Value("${player.password}")
    private String password;

    @Value("${security.password.front.salt}")
    private String frontSalt;

    @Value("${security.password.back.salt}")
    private String backSalt;


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

    public String getTestingEmail(){
        return email;
    }

    public String getTestPassword(){
        return password;
    }

    public String encryptText(String password) {
        String fullText = frontSalt + password + backSalt;

        return "";
    }

    public String decryptText(String password) {

        return "";
    }
}
