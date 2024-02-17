package dgs.reminder.dgs_reg_remind.utils;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AWSDynamoDBUtils {

    public static  <T> Map<String, AttributeValue> convertObjectToPutRequestItem(T object, AWSDynamoDBTables table) throws IllegalAccessException {
        HashMap<String, AttributeValue> map = new HashMap<>();

        for(Field field: object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(object);
            if (value != null){
                if (field.getName().equals("uuid")) {
                    map.put(table.name() + "_" + "UUID", AttributeValue.builder().s(value.toString()).build());
                } else {
                    map.put(field.getName(), AttributeValue.builder().s(value.toString()).build());
                }
            }else{
                map.put(field.getName(),(AttributeValue) value);
            }
        }
        return map;
    }



}
