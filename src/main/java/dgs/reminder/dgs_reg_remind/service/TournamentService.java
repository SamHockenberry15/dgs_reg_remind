package dgs.reminder.dgs_reg_remind.service;

import dgs.reminder.dgs_reg_remind.AuthService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("tournaments")
public class TournamentService {

    @Value("${dgs.reg.browser.driver}")
    private String browserDriver;

    @Value("${dgs.reg.browser.baseTournamentSite}")
    private String baseTournamentSite;

    @Autowired
    private AuthService authService;

    @GetMapping("tournament")
    public void updateTournamentInfo(){

        System.setProperty("webdriver.chrome.driver", browserDriver);
        WebDriver driver = new ChromeDriver();

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
