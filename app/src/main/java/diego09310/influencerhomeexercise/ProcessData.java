package diego09310.influencerhomeexercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diego09310.influencerhomeexercise.data.Influencer;
import diego09310.influencerhomeexercise.data.Action;
import diego09310.influencerhomeexercise.data.messages.FacebookMessage;
import diego09310.influencerhomeexercise.data.messages.Message;
import diego09310.influencerhomeexercise.data.messages.TwitterMessage;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessData {

    private static final Log logger = LogFactory.getLog(ProcessData.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TWITTER_MESSAGE = "TwitterMessage";
    private static final String TWITTER_TOPIC = "twitter";
    private static final String FACEBOOK_TOPIC = "facebook";

    @Autowired
    private InfluencersRepository influencersRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @SneakyThrows
    public void handleData(byte[] data, Class<? extends Message> messageClass) {
        Message message = deserializeMessage(data, messageClass);
        if (message == null) {
            return;
        }
        String topic = TWITTER_MESSAGE.equals(messageClass.getSimpleName()) ? TWITTER_TOPIC : FACEBOOK_TOPIC;

        Influencer influencer = getInfluencer(message);
        Action action = processMessage(influencer, message);

        switch (action) {
            case INDEX:
                kafkaProducer.sendIndexMessage(influencer, topic);
                break;
            case UPDATE:
                kafkaProducer.sendUpdateMessage(influencer, topic);
                break;
            case DELETE:
                kafkaProducer.sendDeleteMessage(influencer, topic);
                break;
            default:
                logger.warn("Discarded: " + mapper.writeValueAsString(message));
        }

    }

    private Message deserializeMessage(byte[] data, Class<? extends Message> messggeClass) {
        String received = new String(data);
        logger.info("Received:" + received);
        try {
            Message msg = mapper.readValue(received, messggeClass);
            logger.info("Deserialized: " + msg.toString());
            return msg;
        } catch(JsonProcessingException ex) {
            logger.error("Couldn't deserialize", ex);
        }
        return null;
    }

    // TODO: Refactor similar Twitter/FB methods
    private Influencer getInfluencer(Message message) {
        if (message instanceof TwitterMessage) {
            return influencersRepository.getInfluencersData().stream()
                    .filter(inf -> isTwitterUpdate(inf, (TwitterMessage)message)
                            || isTwitterProfileForExistingInfluencer(inf, (TwitterMessage) message))
                    .findFirst()
                    .orElse(null);
        }
        return influencersRepository.getInfluencersData().stream()
                .filter(inf -> isFacebookUpdate(inf, (FacebookMessage)message)
                        || isFacebookProfileForExistingInfluencer(inf, (FacebookMessage) message))
                .findFirst()
                .orElse(null);
    }

    private boolean isTwitterUpdate(Influencer influencer, TwitterMessage message) {
        return influencer.getTwitterProfile() != null
                && message.getId() == influencer.getTwitterProfile().getId();
    }

    private boolean isTwitterProfileForExistingInfluencer(Influencer influencer, TwitterMessage message) {
        return influencer.getFacebookProfile() != null
                && StringUtils.equals(message.getScreenName(), influencer.getFacebookProfile().getScreenName());
    }

    private boolean isFacebookUpdate(Influencer influencer, FacebookMessage message) {
        return influencer.getFacebookProfile() != null
                && message.getId() == influencer.getFacebookProfile().getId();
    }

    private boolean isFacebookProfileForExistingInfluencer(Influencer influencer, FacebookMessage message) {
        return influencer.getTwitterProfile() != null
                && StringUtils.equals(message.getUsername(), influencer.getTwitterProfile().getScreenName());
    }

    private Action processMessage(Influencer influencer, Message message) {
        if (message instanceof TwitterMessage) {
            return processTwitterMessage(influencer, (TwitterMessage) message);
        }
        return processFacebookMessage(influencer, (FacebookMessage) message);

    }

    private Action processTwitterMessage(Influencer influencer, TwitterMessage message) {
        if (influencer == null) {
            return Action.INDEX;
        }
        if (influencer.getTwitterProfile() == null) {
            return Action.UPDATE;
        }
        if (message.getDeletedAt() != null && influencer.getFacebookProfile() == null) {
            return Action.DELETE;
        }
        if (message.getUpdatedAt() != null &&
                message.getUpdatedAt().after(influencer.getTwitterProfile().getUpdatedAt())) {
            return Action.UPDATE;
        }
        return Action.DISCARD;
    }

    private Action processFacebookMessage(Influencer influencer, FacebookMessage message) {
        if (influencer == null) {
            return Action.INDEX;
        }
        if (influencer.getFacebookProfile() == null) {
            return Action.UPDATE;
        }
        if (message.getDeletedAt() != null && influencer.getTwitterProfile() == null) {
            return Action.DELETE;
        }
        if (message.getUpdatedAt() != null &&
                message.getUpdatedAt().after(influencer.getFacebookProfile().getUpdatedAt())) {
            return Action.UPDATE;
        }
        return Action.DISCARD;
    }
}
