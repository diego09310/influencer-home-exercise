package diego09310.influencerhomeexercise;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import diego09310.influencerhomeexercise.data.Influencer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Component
public class InfluencersRepository {

    private static final Log logger = LogFactory.getLog(InfluencersRepository.class);

    List<Influencer> influencersData;

    @PostConstruct
    private void setup() {
        try {
            influencersData = new ObjectMapper()
                    .readValue(Paths.get("../data/influencers").toFile(), new TypeReference<>(){});
            logger.info("Influencers file loaded: " + influencersData.toString());
        } catch (IOException e) {
            logger.error("Error trying to load the influencers data", e);
        }
    }

    public List<Influencer> getInfluencersData() {
        return influencersData;
    }


}
