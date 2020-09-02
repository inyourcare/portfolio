package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FcmUtil {

    private static final String firebase_server_key="AAAAYJ6tQ-c:APA91bHP_gPdag28IG3Gl_CgvuBXUWu1vWIBdg24-eXVDFc5Ist9_QQMnoDiXXD7OxLD8iKUhqdbSghEt25RdLVu3d-BGE9rnRPGgX9ksgNBG1DEm9o3ZBQfdz_To4TC1nScfJ6ioeMk";
    private static final String firebase_api_url="https://fcm.googleapis.com/fcm/send";

    public static void send(List<String> tokenList , String title , String body , String pushMessageType) throws UnsupportedEncodingException {
        String notifications = periodicNotificationJson(tokenList,
                new String(title.getBytes("utf-8" ), "iso-8859-1"),
                new String(body.getBytes("utf-8" ), "iso-8859-1"),
                pushMessageType);
        HttpEntity<String> request = new HttpEntity<>(notifications);
        send(request);
    }


    /**
     * 내부함수
     *
     *
     */
    private static String periodicNotificationJson(List<String> tokenList, String title , String body , String pushMessageType) {

        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();

        for(int i=0; i<tokenList.size(); i++) {
            array.add(tokenList.get(i));
        }
        //
        jsonObject.add("registration_ids", array);

        JsonObject notification = new JsonObject();
        notification.addProperty("title", title);
        notification.addProperty("body", body);
        notification.addProperty("pushMessageType",pushMessageType);
        //
        jsonObject.add("notification", notification);

//        System.out.println(body.toString());

        return jsonObject.toString();
    }

    private static void send(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        interceptors.add(new HeaderRequestInterceptor("Authorization",  "key=" + firebase_server_key));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; UTF-8 "));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse = restTemplate.postForObject(firebase_api_url, entity, String.class);

    }
    static class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
        private final String headerName;
        private final String headerValue;

        private HeaderRequestInterceptor(String headerName, String headerValue) {
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpRequest wrapper = new HttpRequestWrapper(request);
            wrapper.getHeaders().set(headerName, headerValue);
            return execution.execute(wrapper, body);
        }
    }
}
