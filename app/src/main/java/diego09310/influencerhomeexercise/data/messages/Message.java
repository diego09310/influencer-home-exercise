package diego09310.influencerhomeexercise.data.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public abstract class Message {
    private int id;
    private String location;
    @JsonProperty("updated_at")
    private Date updatedAt;
    @JsonProperty("deleted_at")
    private Date deletedAt;
}
