package diego09310.influencerhomeexercise.data.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class FacebookMessage extends Message {
    private String username;
    private String about;
    private String image;
}
