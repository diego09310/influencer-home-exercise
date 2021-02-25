package diego09310.influencerhomeexercise.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Influencer {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("_source")
    private Source source;

    @JsonIgnore
    public Profile getTwitterProfile() {
        return this.source.profiles.twitter;
    }
    @JsonIgnore
    public Profile getFacebookProfile() {
        return this.source.profiles.facebook;
    }

    @Data
    public static class Source {
        private Profiles profiles;
        @JsonProperty("updated_at")
        private Date updatedAt;
    }

    @Data
    public static class Profiles {
        private Profile twitter;
        private Profile facebook;
    }

    @Data
    public static class Profile {
        private int id;
        @JsonProperty("screen_name")
        private String screenName;
        private String bio;
        @JsonProperty("updated_at")
        private Date updatedAt;
    }
}
