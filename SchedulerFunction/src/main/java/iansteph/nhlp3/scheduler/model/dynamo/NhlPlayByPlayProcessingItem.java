package iansteph.nhlp3.scheduler.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName="prod-NhlPlayByPlayProcessingAggregate")
public class NhlPlayByPlayProcessingItem {

    private String compositeGameId;
    private int lastProcessedEventIndex;

    public NhlPlayByPlayProcessingItem() {}

    @DynamoDBHashKey
    public String getCompositeGameId() { return compositeGameId; }
    public void setCompositeGameId(final String compositeGameId) { this.compositeGameId = compositeGameId; }

    @DynamoDBAttribute
    public int getLastProcessedEventIndex() { return lastProcessedEventIndex; }
    public void setLastProcessedEventIndex(final int lastProcessedEventIndex) { this.lastProcessedEventIndex = lastProcessedEventIndex; }

    public String toString() {
        return String.format("NhlPlayByPlayProcessingItem(compositeGameId=%s,lastProcessedEventIndex=%s)", compositeGameId,
                lastProcessedEventIndex);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhlPlayByPlayProcessingItem that = (NhlPlayByPlayProcessingItem) o;
        return lastProcessedEventIndex == that.lastProcessedEventIndex &&
                Objects.equals(compositeGameId, that.compositeGameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeGameId, lastProcessedEventIndex);
    }
}