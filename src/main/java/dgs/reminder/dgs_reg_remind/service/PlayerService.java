package dgs.reminder.dgs_reg_remind.service;

import dgs.reminder.dgs_reg_remind.AWSDynamoDBTables;
import dgs.reminder.dgs_reg_remind.AuthService;
import dgs.reminder.dgs_reg_remind.entity.Player;
import dgs.reminder.dgs_reg_remind.repo.PlayerRepo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:auth.properties")
public class PlayerService {

    @Autowired
    private PlayerRepo playerRepo;

    public List<Player> getAllPlayers(){
        return playerRepo.findAll(AWSDynamoDBTables.Player);
    }

    public PutItemResponse addNewPlayer(Player p) {
        PutItemResponse response = null;
        try {
            response = playerRepo.save(p);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return response;
    }
}
