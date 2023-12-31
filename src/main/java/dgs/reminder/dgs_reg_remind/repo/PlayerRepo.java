package dgs.reminder.dgs_reg_remind.repo;

import dgs.reminder.dgs_reg_remind.utils.AWSDynamoDBTables;
import dgs.reminder.dgs_reg_remind.entity.Player;
import dgs.reminder.dgs_reg_remind.utils.AWSDynamoDBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PlayerRepo{

    @Autowired
    DynamoDbClient dynamoDbClient;


    public List<Player> findAll(AWSDynamoDBTables table){
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(table.name())
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        return scanResponse.items().stream().map(map -> {
            Player p = new Player();
            if(map.containsKey("Player_UUID"))
                p.setUuid(map.get("Player_UUID").s());
            if(map.containsKey("name"))
                p.setName(map.get("name").s());
            if(map.containsKey("pdgaNumber"))
                p.setPdgaNumber(map.get("pdgaNumber").s());
            return p;
        }).collect(Collectors.toList());
    }

    public PutItemResponse save(Player p) {
        PutItemResponse putItemResponse = null;
        try {
            if(p.getUuid() == null){
                p.setUuid(UUID.randomUUID().toString());
            }

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(AWSDynamoDBTables.Player.name())
                    .item(AWSDynamoDBUtils.convertObjectToPutRequestItem(p,AWSDynamoDBTables.Player))
                    .build();

            putItemResponse = dynamoDbClient.putItem(putItemRequest);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return putItemResponse;
    }

}
