package dgs.reminder.dgs_reg_remind.service;

import org.apache.catalina.webresources.AbstractResource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("tournaments")
public class TournamentService {

    @Value("${dgs.reg.browser.driver}")
    private String browserDriver;

    @Value("${dgs.reg.browser.baseTournamentSite}")
    private String baseTournamentSite;

    @Autowired
    private AuthService authService;


    //\s+(\d{4})
    private static final String DATE_REGEX = "(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+(\\d{1,2})";
    private String currentPdgaRating = "";

    public void updateTournamentInfo() throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", browserDriver);
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);


        driver.get("https://www.pdga.com/player/144433");
        this.currentPdgaRating = driver.findElement(By.className("current-rating")).getText().replaceAll("Current Rating: ", "").split(" ")[0];


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



        for(String s : tounamentLinks) {

            driver.get(s);

            //only check for registration if not currently registered

            /*
            Options:

            button
            no button and main registration
            button and tiered registration
            no button and tiered registration
            no button and no main registration


             */
            boolean hasMainRegistration = checkForMainRegistration(driver);

            if(!hasMainRegistration)
                checkForTieredRegistration(driver);

            Thread.sleep(1000);
        }

    }

    private boolean checkForMainRegistration(WebDriver driver) {
        try{
            System.out.print(driver.findElement(By.className("tournament-name")).getText() + ":  ");
            System.out.println(driver.findElement(By.className("tournament-registration-opens")).findElement(By.tagName("b")).getText());
            return true;
        } catch (NoSuchElementException e){
            System.out.println("Unable to find Main Registration...");
            return false;
        }

    }

    /*

    //*[@id="maincontent"]/div[4]/table/tbody/tr[1]/td/div/span[1]/b

    /html/body/div[3]/div[2]/div[2]/div[4]/table/tbody/tr[1]/td/div/span[1]/b
     */




    private boolean checkForTieredRegistration(WebDriver driver) {
        try{
            System.out.print(driver.findElement(By.className("tournament-name")).getText() + ":  ");
            List<WebElement> elems = driver.findElement(By.className("registration-schedule-mini")).findElements(By.tagName("tr"));
            String currentMPORegistrationDate = "";
            String previousMPORatingRequirement = "";
            //if last mpo required rating was higher than mine AND the next one has no required rating, set that date for now


            for(WebElement elem : elems){
                List<WebElement> spans = elem.findElements(By.tagName("span"));
                String requiredRatingText = spans.stream()
                        .filter(span -> span.getText().contains("MPO"))
                        .map( span ->  span.getText().replaceAll("[+]", "").replaceAll("MPO", "").replaceAll("\\s", ""))
                        .collect(Collectors.joining());

                if(!requiredRatingText.isEmpty() && Integer.parseInt(this.currentPdgaRating) > Integer.parseInt(requiredRatingText)){
                    currentMPORegistrationDate = elem.findElements(By.tagName("td")).get(0).getText();
                    break;
                }else if(!previousMPORatingRequirement.isEmpty() && requiredRatingText.isEmpty()){
                    currentMPORegistrationDate = elem.findElements(By.tagName("td")).get(0).getText();
                    break;
                }else{
                    previousMPORatingRequirement = requiredRatingText;
                }
            }


            System.out.println(currentMPORegistrationDate);
            return true;
//            System.out.println(driver.findElement(By.xpath("//span[contains(text(),'MPO')]/b")).getText());
        } catch (NoSuchElementException e){
            System.out.println("Unable to find Tiered Registration...");
            return false;
        }
    }

}
