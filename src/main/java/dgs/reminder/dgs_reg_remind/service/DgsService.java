package dgs.reminder.dgs_reg_remind.service;

import dgs.reminder.dgs_reg_remind.AuthService;
import dgs.reminder.dgs_reg_remind.entity.Player;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Service
public class DgsService {

    @Value("${dgs.reg.browser.driver}")
    private String browserDriver;

    //Created until Database is set up... then this will be removed
    @Value("${dgs.reg.browser.tournamentRegistrationSite}")
    private String tournamentRegistrationSite;

    @Autowired
    private AuthService authService;

    @GetMapping("executeTournamentRegistration")
    public String registerForTournament(Player p) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", browserDriver);
        WebDriver driver = new ChromeDriver();
        driver.get(tournamentRegistrationSite);

        driver = authService.login(driver);


        WebElement we  = driver.findElement(By.cssSelector("form[action='" + tournamentRegistrationSite + "']"));

        Map<String, String> personalData = new HashMap<>();
        personalData.put("registrant_1_name", p.getName());
        personalData.put("registrant_1_division_code", p.getDivision());
        personalData.put("registrant_1_pdga_number", p.getPdgaNumber());
        personalData.put("registrant_1_email", p.getEmail());
        personalData.put("registrant_1_phone", p.getPhone());
        personalData.put("registrant_1_city", p.getCity());
        personalData.put("registrant_1_state", p.getState());
        personalData.put("registrant_1_country", p.getCountry());

        Set<String> usedKeys = new HashSet<>();


        //Fill out player information
        WebElement regCon1 = we.findElement(By.id("registrant_1_container"));
        List<WebElement> regCon1Inputs = regCon1.findElements(By.tagName("input"));
        regCon1Inputs.addAll(regCon1.findElements(By.tagName("select")));


        regCon1Inputs.forEach((input) -> {
            try {
                if (input.getAttribute("type").equals("text")) {
                    //All else usual and custom values
                    try {
                        String key = input.getAttribute("id");
                        input.clear();
                        input.sendKeys(personalData.get(key));
                        usedKeys.add(key);
                    } catch (Exception e) {
                        input.sendKeys("N/A");
                    }
                } else if (input.getTagName().equals("select")) {
                    //Division, State, Country
                    try {
                        Select selectElem = new Select(input);
                        String id = input.getAttribute("id");
                        selectElem.selectByValue(personalData.get(id));
                    } catch (Exception e) {
                        System.out.println("issue adding value to following input: " + input);
                    }
                } else if (input.getAttribute("type").equals("checkbox")) {
                    //Just don't do anything here for now.
                }
            } catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        });

        try{
            driver.findElement(By.id("submit-registration-unpaid")).findElement(By.tagName("input")).submit();
        } catch (Exception e){
            System.out.println("Logging out with Paypal button...");
            driver.findElement(By.id("submit-registration-paid")).findElement(By.tagName("input")).submit();
        }

        //Paypal initial button
        driver.findElement(By.xpath("//button[contains(text(),'Log In')]")).click();

        //email section
        driver.findElement(By.id("email")).sendKeys(p.getEmail());
        driver.findElement(By.id("btnNext")).click();

        //need to wait for screen to appear to driver
        Thread.sleep(1000);

        //use password instead of code By.xpath("//a[contains(.,'Log in with a password instead')]")
        driver.findElement(By.xpath("//a[contains(.,'Log in with a password instead')]")).click();

        //enter password and login
        driver.findElement(By.id("password")).sendKeys(p.getPassword());
        driver.findElement(By.id("btnLogin")).submit();

        //Continue to review order
        driver.findElement(By.id("payment-submit-btn")).click();

        //click checkbox for agree to terms
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("input[type='checkbox']")).click();

        driver.findElement(By.id("registration-submit")).submit();

        //submit form: submit_registration_paid OR submit_registration_unpaid

        //Paypal: id - email... button on screen id- btnNext
        // <a> Log in with a password instead... password id - password... id - btnLogin
        //id - payment-submit-btn


        //DGS -> type checkbox.click() -> id: registration-submit.submit()

        System.out.println(driver.getTitle());
        System.out.println();

        return "";
    }



}
