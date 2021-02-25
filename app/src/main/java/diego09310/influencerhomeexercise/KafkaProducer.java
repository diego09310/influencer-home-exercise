package diego09310.influencerhomeexercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diego09310.influencerhomeexercise.data.Influencer;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Log logger = LogFactory.getLog(KafkaProducer.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String JSON_ERROR = "Error serializing influencer";
    private static final String INDEX_FORMAT = "{ \"index\" : {\"_index\" : \"influencers\", \"_retry_on_conflict\" : 3} } \n%s";
    private static final String UPDATE_FORMAT = "{ \"update\" : {\"_index\" : \"influencers\", \"_retry_on_conflict\" : 3, \"_id\" : \"0\"} } \n" +
            "{ \"doc\": %s, \"doc_as_upst\" : true }";
    private static final String DELETE_FORMAT = "{ \"delete\" : {\"_index\" : \"influencers\", \"_retry_on_conflict\" : 3} } \n%s";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendIndexMessage(Influencer influencer, String topic) {
        try {
            String message = String.format(INDEX_FORMAT, mapper.writeValueAsString(influencer));
            this.kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            logger.error(JSON_ERROR, e);
        }
    }

    public void sendUpdateMessage(Influencer influencer, String topic) {
        try {
            String message = String.format(UPDATE_FORMAT, mapper.writeValueAsString(influencer));
            this.kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            logger.error(JSON_ERROR, e);
        }
    }

    @SneakyThrows
    public void sendDeleteMessage(Influencer influencer, String topic) {
        try {
            String message = String.format(DELETE_FORMAT, mapper.writeValueAsString(influencer));
            this.kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            logger.error(JSON_ERROR, e);
        }
    }

}
