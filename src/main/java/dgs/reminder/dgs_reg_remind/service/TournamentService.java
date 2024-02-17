package dgs.reminder.dgs_reg_remind.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tournaments")
public class TournamentService {

    @Value("${dgs.reg.browser.driver}")
    private String browserDriver;

    @Value("${dgs.reg.browser.baseTournamentSite}")
    private String baseTournamentSite;

    @Autowired
    private AuthService authService;

    public void updateTournamentInfo(){

        System.setProperty("webdriver.chrome.driver", browserDriver);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        String myTournamentsSite = baseTournamentSite;
        driver.get(myTournamentsSite);

        driver = authService.login(driver);


        //get main content
        WebElement mainContent = driver.findElement(By.id("maincontent"));
        //get my tournaments
        WebElement myTouneyH1 = mainContent.findElement(By.xpath("//h1[contains(text(),'My tournaments')]"));
        List<WebElement> interestedTournaments = myTouneyH1.findElements(By.xpath("following-sibling::*"));
        //get my tournament links
        List<String> tounamentLinks = interestedTournaments.stream()
                .map((event) -> event.findElement(By.tagName("a")).getAttribute("href"))
                .toList();

        //table.registration-schedule.mini - MPO current rating

        // what do we want to do if we can't figure out when registration is? Send me an email of the list of tournaments that will require manual data manipulation
        //then if in DB then remove it from the email list -- h1.tournament-name



        for(String s : tounamentLinks){
            try{
                driver.get(s);
                System.out.println(driver.findElement(By.className("tournament-registration-opens")).findElement(By.tagName("b")).getText());
            } catch (Exception e){
                System.out.println("unable to get registration information");
            }

        }


    }

}
