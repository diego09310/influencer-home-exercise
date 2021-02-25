package diego09310.influencerhomeexercise;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class InfluencerHomeExerciseApplication implements CommandLineRunner {

    @Autowired
    private NsqService nsqService;

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(InfluencerHomeExerciseApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @SneakyThrows
    @Override
    public void run(String[] args) {
         nsqService.subscribe();
    }

}
