package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import domain.mobile.Credentials;
import domain.mobile.LkkUserCard;
import domain.mobile.UserCard;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeTypeUtils;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import repository.AuthRepository;
import utils.Constants;
import utils.exceptions.Ex;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class AuthService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private final WSClient ws;
    private final Config config;
    private final AuthRepository repository;

    @Inject
    public AuthService(WSClient ws, Config config, AuthRepository repository) {
        this.ws = ws;
        this.config = config;
        this.repository = repository;
    }

    public String getToken(String username, String pwd) throws ExecutionException, InterruptedException {

        try {

            //String url = "http://192.168.100.65:9080/auth/realms/master/protocol/openid-connect/token/";
            String url = "http://localhost:8080/auth/realms/pgk-app/protocol/openid-connect/token/";
            String token = "";

            CompletionStage<WSResponse> response = ws.url(url)
                    .setContentType("application/x-www-form-urlencoded")
                    .post("grant_type=password&username=" + username + "&password=" + pwd + "&client_id=pgk-frontend");

            WSResponse resp = response.toCompletableFuture().get();

            if (resp.getStatus() == Http.Status.OK) {
                String responseString = resp.getBodyAsBytes().utf8String();
                JsonNode jsonNode = Json.parse(responseString);

                if (jsonNode.has("access_token")) {
                    token = jsonNode.get("access_token").asText();
                }
            } else {
                LOG.debug(resp.getStatusText());
            }
            return token;
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public Boolean checkToken(String token) throws ExecutionException, InterruptedException {

        String url = config.getString(Constants.KEYCLOAK_CHECK_TOKEN);

        CompletionStage<WSResponse> response = ws.url(url)
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + token)
                .get();

        WSResponse resp = response.toCompletableFuture().get();

        if (resp.getStatus() == Http.Status.OK) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Credentials getUserByPhone(String phone) {
        return repository.getUserByPhone(phone);
    }

    public UserCard getUserCardByPhone(String phone) {
        return repository.getUserCardByPhone(phone);
    }

    public Boolean sendSms(String phone, String code) {

        return sendSmsToLkk(phone, code);

    }

    private Boolean sendSmsToLkk(String phone, String code) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.createObjectNode();

        ((ObjectNode) node).put("token", "");
        ((ObjectNode) node).put("login", phone);

        if (code != null && !code.equals(""))
        ((ObjectNode) node).put("code", code);

        LOG.debug("json body = "+node.asText());

        WSRequest wsRequest = ws
                .url(Constants.LKK_HOME_URL)
                .setRequestTimeout(Duration.of(10000, ChronoUnit.MILLIS))
                .addHeader(HttpHeaders.ACCEPT, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .setContentType("application/json;charset=utf-8");

        CompletionStage<WSResponse> wsResp = wsRequest.post(node)
                .thenApply(wsResponse -> {
                    LOG.debug("response status = {}", wsResponse.getStatus());
                    LOG.debug("response body = {}", wsResponse.getBody());

                    switch (wsResponse.getStatus()) {
                        case 200: {
                            return wsResponse;
                        }
                        case 201:{
                            return wsResponse;
                        }
                        default: {
                            JsonNode err = wsResponse.asJson();
                            throw Ex.createExecutionException(err.get("error").asText());

                        }
                    }

                });

        ArrayNode result = null;

        try {
            wsResp.toCompletableFuture();
            return Boolean.TRUE;
        } catch (Exception e) {
            throw Ex.createExecutionException(e.getCause().getMessage());
        }
    }

    public CompletionStage<Credentials> createUser(Credentials user) {
        return repository.createUser(user);
    }

    public CompletionStage<Credentials> updateUser(Credentials user) {
        return repository.updateUser(user);
    }

    public void setUserType(String phone, String type) {

        Boolean mr = Boolean.FALSE;
        Boolean lkk = Boolean.FALSE;

        if (type.equals("mr"))
            mr = Boolean.TRUE;

        if (type.equals("lkk"))
            lkk = Boolean.TRUE;

        repository.setUserType(phone, mr, lkk);
    }

    public CompletionStage<UserCard> checkUserCreds(Credentials user) throws InterruptedException {
        Credentials userLkk = new Credentials();

        Credentials userFromDB = getUserByPhone(user.getPhone());

        if (userFromDB == null){
            userLkk = getUserInfo(user.getPhone(), user.getPassword());
            return createUser(userLkk).thenApply(usr-> getUserCardByPhone(usr.getPhone()));
        }

        if (!user.getPassword().equals(userFromDB.getPassword())) {
            userLkk = getUserInfo(user.getPhone(), user.getPassword());
            return updateUser(userLkk).thenApply(usr->getUserCardByPhone(usr.getPhone()));
        }

        return CompletableFuture.supplyAsync(()->new UserCard(userFromDB.getPhone(), userFromDB.getUser(), userFromDB.getCompany()));

    }

    private Credentials getUserInfo(String phone, String pwd) throws InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.createObjectNode();

        ((ObjectNode) node).put("token", "");
        ((ObjectNode) node).put("login", phone);
        ((ObjectNode) node).put("password", pwd);

        WSRequest wsRequest = ws
                .url(Constants.LKK_HOME_URL)
                .setRequestTimeout(Duration.of(10000, ChronoUnit.MILLIS))
                .addHeader(HttpHeaders.ACCEPT, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .setContentType("application/json;charset=utf-8");

        CompletionStage<WSResponse> wsResp = wsRequest.post(node)
                .thenApply(wsResponse -> {
                    LOG.debug("response status = {}", wsResponse.getStatus());
                    LOG.debug("response body = {}", wsResponse.getBody());

                    switch (wsResponse.getStatus()) {
                        case 200: {
                            return wsResponse;
                        }
                        default: {
                            JsonNode err = wsResponse.asJson();
                            throw Ex.createExecutionException(err.get("error").asText());

                        }
                    }

                });

        String result = null;

        try{

        result= StringEscapeUtils.unescapeJava(wsResp.toCompletableFuture().get().getBody());

        } catch (Exception e) {
            throw Ex.createExecutionException( e.getCause().getMessage());
        }

        JsonNode jj = Json.toJson(result);
        LkkUserCard lkk = Json.fromJson(jj, LkkUserCard.class);

        Credentials userCard = new Credentials();
        userCard.setPhone(phone);
        userCard.setUser(lkk.getUser());
        userCard.setCompany(lkk.getCompany());
        return userCard;

    }

}
