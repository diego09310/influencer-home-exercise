package diego09310.influencerhomeexercise;

import com.sproutsocial.nsq.BackoffHandler;
import com.sproutsocial.nsq.Subscriber;
import diego09310.influencerhomeexercise.data.messages.FacebookMessage;
import diego09310.influencerhomeexercise.data.messages.TwitterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NsqService {

    private static final String CHANNEL = "channels";
    private static final String TWITTER_TOPIC = "twitter";
    private static final String FACEBOOK_TOPIC = "facebook";

    @Autowired
    private ProcessData processData;

    // TODO: Handle ConnectException
    public void subscribe() {
        Subscriber subscriber = new Subscriber("localhost");
        subscriber.subscribe(TWITTER_TOPIC, CHANNEL, new BackoffHandler(msg -> handleTwitterData(msg.getData())));
        subscriber.subscribe(FACEBOOK_TOPIC, CHANNEL, new BackoffHandler(msg -> handleFacebookData(msg.getData())));
    }

    private void handleTwitterData(byte[] data) {
        processData.handleData(data, TwitterMessage.class);
    }

    private void handleFacebookData(byte[] data) {
        processData.handleData(data, FacebookMessage.class);
    }

}
