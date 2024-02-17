package dgs.reminder.dgs_reg_remind.controller;

import dgs.reminder.dgs_reg_remind.entity.Player;
import dgs.reminder.dgs_reg_remind.service.DgsService;
import dgs.reminder.dgs_reg_remind.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController("remind")
public class DgsSiteController {

    @Autowired
    private DgsService dgsService;

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("executeTournamentRegistration")
    public void registerForTournament(@RequestParam boolean test) throws InterruptedException {
        if(test) {
            Player p = new Player();
            p.setName("TestAccount"+System.currentTimeMillis());
            p.setPdgaNumber("123");
            p.setDivision("MPO");
            p.setEmail("Test@gmail.com");
            p.setCity("TestCity");
            p.setState("PA");
            p.setCountry("US");
            p.setPhone("1112223333");
            dgsService.registerForTournament(p);
        } else {
            System.out.println("Still in testing mode... need testing flag active to register for tournament..");
        }
    }

    @GetMapping("executeCollectTournamentData")
    public void collectTournamentData(@RequestParam boolean test) throws InterruptedException {
        if(test){
            tournamentService.updateTournamentInfo();
        }else {
            System.out.println("Still in testing mode... need testing flag active to collect data..");
        }
    }


}
