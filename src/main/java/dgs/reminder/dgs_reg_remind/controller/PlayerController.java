package dgs.reminder.dgs_reg_remind.controller;

import dgs.reminder.dgs_reg_remind.entity.Player;
import dgs.reminder.dgs_reg_remind.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.List;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("player")
    public List<Player> getAllPlayers(){
        return playerService.getAllPlayers();
    }

    @PostMapping("addPlayer")
    public String addNewPlayer(@RequestBody Player p){
       return playerService.addNewPlayer(p).statusText().get();
    }
}
