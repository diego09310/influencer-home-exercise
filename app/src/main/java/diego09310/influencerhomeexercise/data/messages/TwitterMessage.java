package diego09310.influencerhomeexercise.data.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class TwitterMessage extends Message {
    @JsonProperty("screen_name")
    private String screenName;
    private String biography;
    @JsonProperty("profile_image")
    private String profileImage;
}
