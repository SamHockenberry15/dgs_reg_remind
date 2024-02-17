package dgs.reminder.dgs_reg_remind.service;

import dgs.reminder.dgs_reg_remind.utils.AWSDynamoDBTables;
import dgs.reminder.dgs_reg_remind.entity.Player;
import dgs.reminder.dgs_reg_remind.entity.PlayerCredential;
import dgs.reminder.dgs_reg_remind.repo.PlayerCredentialRepository;
import dgs.reminder.dgs_reg_remind.repo.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private AuthService authService;

    @Autowired
    private PlayerRepo playerRepo;

    @Autowired
    private PlayerCredentialRepository playerCredentialRepository;

    public List<Player> getAllPlayers(){
        return playerRepo.findAll(AWSDynamoDBTables.Player);
    }

    public SdkHttpResponse addNewPlayer(Player p) {
        SdkResponse response;
        try {
            response = playerRepo.save(p);
            if (response != null && response.sdkHttpResponse().isSuccessful()){
                PlayerCredential pc = new PlayerCredential(p.getUuid(),authService.encryptText(p.getEmail()), authService.encryptText(p.getPassword()));
                playerCredentialRepository.save(pc);
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return response.sdkHttpResponse();
    }
}
