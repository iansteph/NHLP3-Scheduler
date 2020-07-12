package iansteph.nhlp3.scheduler.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Map;
import java.util.Objects;

@DynamoDBTable(tableName="prod-NhlPlayByPlayProcessingAggregate")
public class ShiftPublishingItem {

    private String PK;
    private String SK;
    private Map<String, Map<String, Integer>> shiftPublishingRecord;

    @DynamoDBHashKey
    public String getPK() {

        return PK;
    }

    public void setPK(final String PK) {

        this.PK = PK;
    }

    @DynamoDBRangeKey
    public String getSK() {

        return SK;
    }

    public void setSK(final String SK) {

        this.SK = SK;
    }

    @DynamoDBAttribute
    public Map<String, Map<String, Integer>> getShiftPublishingRecord() {

        return shiftPublishingRecord;
    }

    public void setShiftPublishingRecord(final Map<String, Map<String, Integer>> shiftPublishingRecord) {

        this.shiftPublishingRecord = shiftPublishingRecord;
    }

    @Override
    public String toString() {

        return "ShiftPublishingItem{" +
                "PK='" + PK + '\'' +
                ", SK='" + SK + '\'' +
                ", shiftPublishingRecord=" + shiftPublishingRecord +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftPublishingItem that = (ShiftPublishingItem) o;
        return Objects.equals(PK, that.PK) &&
                Objects.equals(SK, that.SK) &&
                Objects.equals(shiftPublishingRecord, that.shiftPublishingRecord);
    }

    @Override
    public int hashCode() {

        return Objects.hash(PK, SK, shiftPublishingRecord);
    }
}
