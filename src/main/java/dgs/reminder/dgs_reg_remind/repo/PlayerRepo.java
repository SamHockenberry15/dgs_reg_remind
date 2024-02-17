package dgs.reminder.dgs_reg_remind.repo;

import dgs.reminder.dgs_reg_remind.utils.AWSDynamoDBTables;
import dgs.reminder.dgs_reg_remind.entity.Player;
import dgs.reminder.dgs_reg_remind.utils.AWSDynamoDBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public SdkResponse save(Player p) {
        SdkResponse response;
        try {
            if(p.getUuid() == null){
                p.setUuid(UUID.randomUUID().toString());
            }

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(AWSDynamoDBTables.Player.name())
                    .item(AWSDynamoDBUtils.convertObjectToPutRequestItem(p,AWSDynamoDBTables.Player))
                    .build();

            ScanRequest scanRequest = ScanRequest.builder().tableName(AWSDynamoDBTables.Player.name())
                    .filterExpression("pdgaNumber = :pdgaNumber")
                    .expressionAttributeValues(Map.of(":pdgaNumber", AttributeValue.builder().s(p.getPdgaNumber()).build()))
                    .build();

            ScanResponse sresp = dynamoDbClient.scan(scanRequest);

            if(sresp.count() > 0){
                System.out.println("Player already added to DB!");
                response = PutItemResponse.builder()
                        .sdkHttpResponse(SdkHttpResponse.builder()
                                .statusCode(200)
                                .statusText("Player already added to DB!")
                                .build())
                        .build();
            }else{
                response = dynamoDbClient.putItem(putItemRequest);
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        return response;
    }

}
