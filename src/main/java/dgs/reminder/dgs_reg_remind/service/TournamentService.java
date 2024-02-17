package dgs.reminder.dgs_reg_remind.service;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private String currentPdgaRating = "";

    public void updateTournamentInfo() throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", browserDriver);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
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

        List<String> eventsUnableToFindRegInfo = new ArrayList<>();
        //table.registration-schedule.mini - MPO current rating

        // what do we want to do if we can't figure out when registration is? Send me an email of the list of tournaments that will require manual data manipulation
        //then if in DB then remove it from the email list -- h1.tournament-name



        for(String link : tounamentLinks) {

            //check for registration
            driver.get(link);
            String tournamentName = driver.findElement(By.className("tournament-name")).getText();
            System.out.println(tournamentName + ":  ");

            boolean isRegistered = checkForPlayerRegistration(driver, link);

            driver.get(link);

            //player is not registered
            if(!isRegistered){
                //if tournament is already open do we want to register now?
                //should we have a DB of already registered tournaments?
                boolean isTournamentAlreadyOpen = checkForOpenRegistration(driver);
                boolean isTournamentMainRegistration = checkForMainRegistration(driver);
                boolean isTournamentTieredRegistration = checkForTieredRegistration(driver);
                boolean tournamentDateFound = false;

                if(isTournamentAlreadyOpen && !isTournamentTieredRegistration){
                    System.out.println("Need to determine what to do here...");
                    //register for tournament?
                }else if(!isTournamentAlreadyOpen && isTournamentMainRegistration){
                    String date = findMainRegistrationDate(driver);
                    tournamentDateFound = true;
                    System.out.println(date);
                }else if(!isTournamentAlreadyOpen && isTournamentTieredRegistration){
                    String date = findTieredRegistrationDate(driver);
                    tournamentDateFound = true;
                    System.out.println(date);
                }else if(isTournamentAlreadyOpen && isTournamentTieredRegistration){
                    //find date then sign up immediately?
                    System.out.println("Need to find date and determine what to do here...");
                }

                if(!tournamentDateFound)
                    eventsUnableToFindRegInfo.add(tournamentName);

                Thread.sleep(1000);
            }

            /*
            Options:

            button
            no button and main registration
            button and tiered registration
            no button and tiered registration
            no button and no main registration


             */
        }
        System.out.println();
        System.out.println("Unable to get reg for:  ");
        eventsUnableToFindRegInfo.forEach(System.out::println);

    }

    private boolean checkForPlayerRegistration(WebDriver driver, String link) throws InterruptedException {
        driver.get(link+"/registration");

        try{
            WebElement e = driver.findElement(By.className("registration-member"));
            System.out.println("Already registered... skipped!");
            return true;
        }catch (Exception e){
            return false;
        }

    }

    private boolean checkForOpenRegistration(WebDriver driver) {
        //tournament-register-link for button
        try {
            driver.findElement(By.className("tournament-register-link"));
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean checkForMainRegistration(WebDriver driver) {
        try{
            driver.findElement(By.className("tournament-registration-opens")).findElement(By.tagName("b"));
            return true;
        } catch (NoSuchElementException e){
            return false;
        }
    }

    private boolean checkForTieredRegistration(WebDriver driver) {
        try{
            driver.findElement(By.className("registration-schedule-mini"));
            return true;
        } catch (NoSuchElementException e){
            return false;
        }
    }

    private String findMainRegistrationDate(WebDriver driver){
        try{
            return driver.findElement(By.className("tournament-registration-opens")).findElement(By.tagName("b")).getText();
        } catch (NoSuchElementException e){
            return "Cannot get main registration date for tournament...";
        }
    }

    private String findTieredRegistrationDate(WebDriver driver) {
        try{
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

                if(!requiredRatingText.isEmpty() && Integer.parseInt(this.currentPdgaRating) > Integer.parseInt(requiredRatingText)) {
                    currentMPORegistrationDate = elem.findElements(By.tagName("td")).get(0).getText();
                    break;
                }else if(!previousMPORatingRequirement.isEmpty() && requiredRatingText.isEmpty()){
                    currentMPORegistrationDate = elem.findElements(By.tagName("td")).get(0).getText();
                    break;
                }else{
                    previousMPORatingRequirement = requiredRatingText;
                    currentMPORegistrationDate = elem.findElements(By.tagName("td")).get(0).getText();
                }
            }

            return currentMPORegistrationDate.replace("\n", " ").replace("at ", "");
        } catch (NoSuchElementException e){
            return "Cannot find tierd registration date...";
        }
    }

}
