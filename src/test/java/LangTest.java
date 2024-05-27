import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;

public class LangTest {
    @Test
    public void test() {
        ServerResponse res = WebClient.create("http://192.168.23.202:8081/")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    return ServerResponse.ok().bodyValue("ok");
                }).block();
        System.err.println(res.statusCode().value());
    }
}
