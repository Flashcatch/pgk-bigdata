package controllers;

import akka.actor.ActorSystem;
import com.cloudera.impala.jdbc41.DataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import domain.bigdata.AttributeList;
import domain.bigdata.BigDataQueryResponse;
import domain.bigdata.BigDataQueryResponseList;
import domain.bigdata.GroupingSets;
import dto.bigdata.BigDataQueryDto;
import dto.bigdata.BigDataQueryParamsDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import play.cache.AsyncCacheApi;
import play.cache.Cached;
import play.cache.NamedCache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import utils.SiCalcExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static utils.Constants.ABSENT_METRIX;
import static utils.Constants.ATTRIBUTE_LIST;
import static utils.Constants.FREIGHTS;
import static utils.Constants.GROUPING_SET;
import static utils.Constants.IMPALA_URL;
import static utils.Constants.NTH_LINE;
import static utils.Constants.SICALCULATION_QUERY;
import static utils.Constants.STATIONS;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 *
 * @author SandQ
 */

@Slf4j
@Api(value = "Redis контроллер", produces = "application/json")
public class RedisController extends Controller {


    @NamedCache("local")
    private final AsyncCacheApi asyncCacheApi;

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    private final Config config;

    private final SiCalcExecutionContext ec;

    @Inject
    public RedisController(AsyncCacheApi asyncCacheApi,
                           ActorSystem actorSystem,
                           ExecutionContext executionContext,
                           Config config,
                           SiCalcExecutionContext ec) {
        this.asyncCacheApi = asyncCacheApi;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.config = config;
        this.ec = ec;
    }

    /**
     * @return stub
     */
    public Result index() {
        return ok("Application is ready. ");
    }

    /**
     * @return CompletionStageResult result.
     *
     */
    @ApiOperation(value = "Получение метрик из si_calculation, используя redis",
            consumes = "application/json",
            produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "logs",
                    dataType = "boolean",
                    paramType = "query",
                    value = "Выводить данные в лог",
                    defaultValue = "false"
            ),
            @ApiImplicitParam(
                    name = "body",
                    dataType = "dto.bigdata.BigDataQueryDto",
                    paramType = "body",
                    value = "Тело запроса"
            )
    })
    @SuppressWarnings("unchecked")
    @Cached(key = "siCalcResults", duration = 15)
    public CompletionStage<Result> getSiCalculationRedis() {
        return asyncCacheApi.getOrElseUpdate("siCalcResults", () -> {
            log.debug("<< not in cache, executing");
            boolean logs = Boolean.parseBoolean(request().getQueryString("logs"));

            if (logs) {
                log.debug("<< getSiCalculationRedis < start, now:{}", now());
            }

            BigDataQueryResponseList response = new BigDataQueryResponseList();
            List<BigDataQueryResponse> responseList = new ArrayList<>();

            //log.debug("<< getSiCalculationRedis < Processing request data");
            JsonNode json = request().body().asJson();
            //log.debug("<< getSiCalculationRedis < json: {}", json);

            return supplyAsync(() -> {
                DataSource ds = new DataSource();
                ds.setURL(IMPALA_URL + config.getString("impala.host") + ":" + config.getString("impala.port"));

                try (Connection connection = ds.getConnection()) {

                    // По ключу grsets получаем grouping_set_id, и уровень метрики
                    List<GroupingSets> groupingSetsList = (List<GroupingSets>) asyncCacheApi.get("grsets")
                            .toCompletableFuture().join();

                    if (logs) {
                        log.debug("grsets populated");
                    }

                    BigDataQueryDto body = Json.fromJson(json, BigDataQueryDto.class);
                    //log.debug("<< getSiCalculationRedis < json parsed,body: {}", body);

                    // Получаем список полей
                    List<AttributeList> attributeList = getAttributeList(connection, false, logs)
                            .toCompletableFuture().join();

                    if (logs) {
                        log.debug("attributeList populated");
                    }

                    // Цикл по json данным
                    List<BigDataQueryParamsDto> metricsBlanks = null;

                    if (body.getMetricsBlanks() != null) {
                        metricsBlanks = body.getMetricsBlanks();
                    } else {
                        return completedFuture(notAcceptable("empty json!"));
                    }

                    response.setActualDate(body.getActualDate());

                    if (logs) {
                        log.debug("json elements:{}", metricsBlanks.size());
                    }

                    for (BigDataQueryParamsDto params : metricsBlanks) {

                        long groupingSetId = -1;
                        double duration = ABSENT_METRIX;
                        String key = "";
                        StringBuilder keyBuilder = new StringBuilder();

                        int calcLvl = -1;

                        List<GroupingSets> grSets = groupingSetsList.stream()
                                .filter(data -> data.getStatIndicatorId() == params.getId())
                                .collect(Collectors.toList());

                        // Получаем отделения и дороги по id станции
                        Long sndRwId = 0L;
                        Long sndDpId = 0L;
                        Long rsvRwId = 0L;
                        Long rsvDpId = 0L;

                        if (params.getId() == 1 || params.getId() == 2 || params.getId() == 13 || params.getId() == 14) {

                            sndRwId = (Long) asyncCacheApi.get("station:rw:" + params.getSndStId()).toCompletableFuture().join();
                            sndDpId = (Long) asyncCacheApi.get("station:dp:" + params.getSndStId()).toCompletableFuture().join();
                            rsvRwId = (Long) asyncCacheApi.get("station:rw:" + params.getRsvStId()).toCompletableFuture().join();
                            rsvDpId = (Long) asyncCacheApi.get("station:dp:" + params.getRsvStId()).toCompletableFuture().join();

                        }

                        for (GroupingSets groupingSets : grSets) {

                            groupingSetId = groupingSets.getGroupingSetId();

                            // фильтруем список атрибутов для текущего уровня
                            final long grSetId = groupingSetId;
                            List<AttributeList> attrs = attributeList.stream()
                                    .filter(data -> data.getGroupingSetId() == grSetId)
                                    .collect(Collectors.toList());

                            key = "sicalculation:grouping_set_id:" + groupingSetId + ":year_month:" + body.getActualDate();
                            keyBuilder.append(key);

                            // TODO: ПРОВЕРИТЬ ЧТО ВО ВХОДНОМ JSON ЕСТЬ ВСЕ ПОЛЯ, УКАЗАННЫЕ В ATTRIBUTE_LIST !
                            for (AttributeList attrList : attrs) {

                                String sqlCalcName = attrList.getSqlCalcName();

                                if (params.getId() == 1 || params.getId() == 2 || params.getId() == 13 || params.getId() == 14) {

                                    if ("SND_RW_ID".equals(sqlCalcName) && sndRwId != null) {
                                        keyBuilder.append(":snd_rw_id:").append(sndRwId);
                                    } else if ("RSV_RW_ID".equals(sqlCalcName) && rsvRwId != null) {
                                        keyBuilder.append(":rsv_rw_id:").append(rsvRwId);
                                    } else if ("SND_DP_ID".equals(sqlCalcName) && sndDpId != null) {
                                        keyBuilder.append(":snd_dp_id:").append(sndDpId);
                                    } else if ("RSV_DP_ID".equals(sqlCalcName) && rsvDpId != null) {
                                        keyBuilder.append(":rsv_dp_id:").append(rsvDpId);
                                    }
                                }

                                if ("SND_ST_ID".equals(sqlCalcName) && params.getSndStId() != null) {
                                    keyBuilder.append(":snd_st_id:").append(params.getSndStId());
                                } else if ("RSV_ST_ID".equals(sqlCalcName) && params.getRsvStId() != null) {
                                    keyBuilder.append(":rsv_st_id:").append(params.getRsvStId());
                                } else if ("SND_ORG_ID".equals(sqlCalcName) && params.getSndOrgId() != null) {
                                    keyBuilder.append(":snd_org_id:").append(params.getSndOrgId());
                                } else if ("RSV_ORG_ID".equals(sqlCalcName) && params.getRsvOrgId() != null) {
                                    keyBuilder.append(":rsv_org_id:").append(params.getRsvOrgId());
                                } else if ("FR_ID".equals(sqlCalcName) && params.getFrId() != null) {
                                    keyBuilder.append(":fr_id:").append(params.getFrId());
                                } else if ("ROD_ID".equals(sqlCalcName) && params.getRodId() != null) {
                                    keyBuilder.append(":rod_id:").append(params.getRodId());
                                } else if ("ROUTE_SEND_SIGN".equals(sqlCalcName) && params.getRouteSendSign() != null) {
                                    keyBuilder.append(":route_send_sign:").append(params.getRouteSendSign());
                                } else if ("CLIENT_ID".equals(sqlCalcName) && params.getClientId() != null) {
                                    keyBuilder.append(":client_id:").append(params.getClientId());
                                } else if ("VID_PODGOTOVKI".equals(sqlCalcName) && params.getVidPodgotovki() != null) {
                                    keyBuilder.append(":vid_podgotovki:").append(params.getVidPodgotovki());
                                } else if ("VID_ZABRAKOVKI".equals(sqlCalcName) && params.getVidZabrakovki() != null) {
                                    keyBuilder.append(":vid_zabrakovki:").append(params.getVidZabrakovki());
                                } else if ("IS_TECH_ST".equals(sqlCalcName) && params.getIsTechSt() != null) {
                                    keyBuilder.append(":is_tech_st:").append(params.getIsTechSt());
                                } else if ("ISLOAD".equals(sqlCalcName) && params.getIsLoad() != null) {
                                    keyBuilder.append(":isload:").append(params.getIsLoad());
                                } else if ("MODEL_PROPERTY_ID".equals(sqlCalcName) && params.getModelPropertyId() != null) {
                                    keyBuilder.append(":model_property_id:").append(params.getModelPropertyId());
                                } else if ("ST_ID".equals(sqlCalcName) && params.getStIdDisl() != null) {
                                    keyBuilder.append(":st_id:").append(params.getStIdDisl());
                                }

                                if ((params.getId() == 3 ||
                                        params.getId() == 4 ||
                                        params.getId() == 6 ||
                                        params.getId() == 7 ||
                                        params.getId() == 15) && ("FR_GROUP_ID".equals(sqlCalcName) && params.getFrId() != null)) {
                                    Long frGrId = (Long) asyncCacheApi.get("freight:gr:" + params.getFrId()).toCompletableFuture().join();
                                    keyBuilder.append(":fr_group_id:").append(frGrId);
                                }
                            }

                            // Ключ построен
                            key = keyBuilder.toString();
                            keyBuilder.setLength(0); // set length of buffer to 0
                            keyBuilder.trimToSize(); // trim the underlying buffer

                            calcLvl = groupingSets.getLevel();

                            if (logs) {
                                log.debug("key={} on level:{}", key, calcLvl);
                            }

                            // Проверяем json на полноту данных
                            //if (logs) {
                            //    log.debug(">> GETTING DURATION FROM CACHE <<");
                            //}

                            Object cachedObj = asyncCacheApi.get(key).toCompletableFuture().join();

                            if (cachedObj == null) {
                                duration = ABSENT_METRIX;
                            } else {
                                duration = (double) cachedObj;
                            }

                            //if (logs) {
                            //    log.debug(">> GETTING DURATION FROM CACHE DONE, duration:{} <<", duration);
                            //}

                            if (duration > 0) {
                                if (logs) {
                                    log.debug(">> duration:{} found on level:{} <<", duration, calcLvl);
                                }
                                break;
                            }

                            //if (logs) {
                            //  log.debug("grset {} row processed", groupingSetId);
                            //}

                        } // Прошли все уровни

                        // TODO: ДОБАВИТЬ ВСЕ ПОЛЯ ИЗ ВХОДНОГО JSON
                        BigDataQueryResponse bdResponse = BigDataQueryResponse.builder()
                                .id(params.getId())
                                .duration(String.format("%1$,.2f", duration))
                                //.sndStId(params.getSndStId())
                                //.rsvStId(params.getRsvStId())
                                //.rodId(params.getRodId())
                                //.routeSendSign(params.getRouteSendSign())
                                .calcLevel(calcLvl)
                                .build();

                        if (duration == ABSENT_METRIX) {
                            bdResponse.setDuration(String.format("%1$,.2f", ABSENT_METRIX));
                            bdResponse.setException("duration is null! key=" + key);
                        }

                        responseList.add(bdResponse);

                        if (logs) {
                            log.debug("json row processed");
                        }
                    }

                } catch (SQLException e) {
                    log.error("getStatIndicators sql error:{} ", e.getMessage());
                }
                response.setMetrics(responseList);

                if (logs) {
                    log.debug("<< getSiCalculationRedis < end, now:{}", now());
                }
                return response;
            }, ec).thenApply(res -> ok(Json.toJson(response)));
        },15);
    }

    /**
     * @param key key
     * @return Result
     */
    @ApiOperation(value = "get")
    public CompletionStage<Result> get(@ApiParam(value = "key", required = true) String key) {
        CompletionStage<Object> result = asyncCacheApi.get(key);
        return result.thenApply(v -> ok(Json.toJson(v)));

    }

    /**
     * @param key key
     * @return Result
     */
    @ApiOperation(value = "set", consumes = "application/json")
    @ApiImplicitParams(
            {@ApiImplicitParam(
                    name = "body",
                    dataType = "String",
                    required = true,
                    paramType = "body",
                    value = "Данные для сохранения в Redis"
            )}
    )
    public CompletionStage<Result> set(@ApiParam(value = "key", required = true) String key) {
        JsonNode value = request().body().asJson();
        if (value == null) {
            return supplyAsync(() -> notAcceptable("body"));
        }

        return asyncCacheApi.set(key, value).thenApply(done -> created("Created"));

    }

    /**
     * @param key key
     * @return Result
     */
    @ApiOperation(value = "remove")
    public CompletionStage<Result> remove(@ApiParam(value = "key", required = true) String key) {
        return asyncCacheApi.remove(key).thenCompose(done -> remove("Removed"));
    }

    /**
     * Remove all data from redis.
     *
     * @return Result
     */
    @ApiOperation(value = "removeAll")
    public CompletionStage<Result> removeAll() {
        return asyncCacheApi.removeAll().thenApply(res -> ok());
    }

    /**
     * @return Result
     * @author SandQ
     */
    @ApiOperation(value = "Перегрузить данные из таблиц kudu в redis")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "refresh",
                    dataType = "boolean",
                    paramType = "query",
                    defaultValue = "true",
                    value = "Полный сброс кеша"
            ),
            @ApiImplicitParam(
                    name = "reloadSiCalculation",
                    dataType = "boolean",
                    paramType = "query",
                    defaultValue = "false",
                    value = "Перевыгрузить si_calculation(true) или перевыгрузить только справочники(false)"
            ),
            @ApiImplicitParam(
                    name = "groupingSetId",
                    dataType = "int",
                    paramType = "query",
                    defaultValue = "0",
                    value = "Загрузить в редис si_calculation c указанным срезом. 0 - Грузить все"
            )
    })
    public Result reloadKuduToRedis() {

        // получаем параметры запроса
        boolean refresh = Boolean.parseBoolean(request().getQueryString("refresh"));
        boolean reloadSiCalculation = Boolean.parseBoolean(request().getQueryString("reloadSiCalculation"));
        int groupingSetId = Integer.parseInt(request().getQueryString("groupingSetId"));

        DataSource ds = new DataSource();
        ds.setURL(IMPALA_URL + config.getString("impala.host") + ":" + config.getString("impala.port"));

        this.actorSystem.scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.SECONDS), // delay
                () -> {

                    log.debug(">> reloadKuduToRedis > Akka scheduler started:{}", now());

                    // Очищаем все ключи в кеше и ждем полной очистки
                    if (refresh) {
                        asyncCacheApi.removeAll().toCompletableFuture().join();
                    }

                    try (Connection connection = ds.getConnection()) {

                        // 1) Грузим stations и frights в кеш
                        transferStations(connection);
                        transferFreights(connection);
                        // 2) Грузим groupingSets
                        List<GroupingSets> sets = transferGroupingSets(connection);
                        if (reloadSiCalculation) {
                            // 3) Грузим si_calculation
                            transferSiCalculation(connection, groupingSetId);
                        }

                    } catch (SQLException e) {
                        e.iterator()
                                .forEachRemaining(err -> log.error("<< getSiCalculationRedis > error: {}", err.getMessage()));
                    }

                    log.debug(">> reloadKuduToRedis > end:{}", now());

                },
                this.executionContext);

        return ok("reload job started");
    }

    // !!
    private void transferStations(Connection connection) {
        log.debug("transferring stations to cache, start:{}", now());
        String key = "station:";

        try (PreparedStatement st = connection.prepareStatement(STATIONS);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                asyncCacheApi.set(key + "rw:" + rs.getLong(1), rs.getLong(2));
                asyncCacheApi.set(key + "dp:" + rs.getLong(1), rs.getLong(3));
            }

        } catch (SQLException e) {
            log.error("stations sql error:{} ", e.getMessage());
            asyncCacheApi.remove(key);
        }

        log.debug("transferring stations to cache, end:{}", now());
    }

    private void transferFreights(Connection connection) {
        log.debug("transferring freights to cache, start:{}", now());
        String key = "freight:gr:";
        try (PreparedStatement st = connection.prepareStatement(FREIGHTS);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                String k = key + rs.getLong(1);
                asyncCacheApi.set(k, rs.getLong(2));
            }

        } catch (SQLException e) {
            log.error("freights sql error:{} ", e.getMessage());
        }

        log.debug("transferring freights to cache, end:{}", now());
    }

    // !!
    private List<GroupingSets> transferGroupingSets(Connection connection) {
        log.debug("transferring Grouping Sets , start:{}", now());

        String key = "grsets";
        asyncCacheApi.remove(key);
        List<GroupingSets> sets = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(GROUPING_SET);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                GroupingSets s = GroupingSets.builder()
                        .statIndicatorId(rs.getLong(1))
                        .groupingSetId(rs.getLong(2))
                        .level(rs.getInt(3))
                        .build();
                sets.add(s);
            }
            asyncCacheApi.set(key, sets);
        } catch (SQLException e) {
            log.error("getGroupingSets query exec error:{} ", e.getMessage());
            asyncCacheApi.remove(key);

        }
        log.debug("transferring Grouping Sets to cache, end:{}", now());
        return sets;
    }

    private void transferSiCalculation(Connection connection, int groupingSetIdGlb) {
        log.debug("transferring Si Calculation , start:{}", now());

        final String key = "sicalculation";
        int nthLine = NTH_LINE; // print every n-th line
        int counter = 0;

        try (PreparedStatement st = connection.prepareStatement(groupingSetIdGlb == 0 ?
                SICALCULATION_QUERY : SICALCULATION_QUERY + " where grouping_set_id = " + groupingSetIdGlb);
             ResultSet rs = st.executeQuery()) {

            String k;
            List<AttributeList> attributeList = getAttributeList(connection, true, false)
                    .toCompletableFuture()
                    .join();

            while (rs.next()) {

                counter++;

                long groupingSetId = rs.getLong(1);
                k = key + ":grouping_set_id:" + groupingSetId + ":year_month:" + rs.getDate(2);
                // Ключ формируется из параметров, валидных для этого уровня.
                // Таблицы si_grouping_set_elem и таблица attribute_list

                List<AttributeList> attrs = attributeList.stream()
                        .filter(data -> data.getGroupingSetId() == groupingSetId)
                        .collect(Collectors.toList());

                StringBuilder keyBuilder = new StringBuilder(k);

                for (AttributeList attrList : attrs) {

                    String sqlCalcName = attrList.getSqlCalcName();

                    if ("SND_CN_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":snd_cn_id:").append(rs.getLong(3));
                    } else if ("RSV_CN_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":rsv_cn_id:").append(rs.getLong(4));
                    } else if ("SND_DP_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":snd_dp_id:").append(rs.getLong(5));
                    } else if ("RSV_DP_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":rsv_dp_id:").append(rs.getLong(6));
                    } else if ("SND_RW_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":snd_rw_id:").append(rs.getLong(7));
                    } else if ("RSV_RW_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":rsv_rw_id:").append(rs.getLong(8));
                    } else if ("SND_ST_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":snd_st_id:").append(rs.getLong(9));
                    } else if ("RSV_ST_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":rsv_st_id:").append(rs.getLong(10));
                    } else if ("SND_ORG_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":snd_org_id:").append(rs.getLong(11));
                    } else if ("RSV_ORG_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":rsv_org_id:").append(rs.getLong(12));
                    } else if ("FR_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":fr_id:").append(rs.getLong(13));
                    } else if ("FR_GROUP_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":fr_group_id:").append(rs.getLong(14));
                    } else if ("ISLOAD".equals(sqlCalcName)) {
                        keyBuilder.append(":isload:").append(rs.getLong(15));
                    } else if ("ROD_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":rod_id:").append(rs.getLong(16));
                    } else if ("SEND_KIND_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":send_kind_id:").append(rs.getLong(17));
                    } else if ("PARK_SIGN".equals(sqlCalcName)) {
                        keyBuilder.append(":park_sign:").append(rs.getLong(18));
                    } else if ("ROUTE_SEND_SIGN".equals(sqlCalcName)) {
                        keyBuilder.append(":route_send_sign:").append(rs.getLong(19));
                    } else if ("MODEL_PROPERTY_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":model_property_id:").append(rs.getLong(20));
                    } else if ("ST_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":st_id:").append(rs.getLong(21));
                    } else if ("CLIENT_ID".equals(sqlCalcName)) {
                        keyBuilder.append(":client_id:").append(rs.getLong(22));
                    } else if ("VID_PODGOTOVKI".equals(sqlCalcName)) {
                        keyBuilder.append(":vid_podgotovki:").append(rs.getString(23));
                    } else if ("VID_ZABRAKOVKI".equals(sqlCalcName)) {
                        keyBuilder.append(":vid_zabrakovki:").append(rs.getLong(24));
                    } else if ("IS_TECH_ST".equals(sqlCalcName)) {
                        keyBuilder.append(":is_tech_st:").append(rs.getLong(25));
                    }

                }
                k = keyBuilder.toString();
                asyncCacheApi.set(k, rs.getDouble(26));
                if (counter % nthLine == 0) { // Check if the line is a full multiple of your nthLine
                    log.debug("Si Calculation , counter={} rows processed", counter);
                }
            }

        } catch (SQLException e) {
            log.error("Si Calculation query exec error:{} ", e.getMessage());
            e.printStackTrace();
        }
        log.debug("transferring SiCalculation to cache, end:{}, total rows processed:{}", now(), counter);
    }

    /**
     * getting attribute list.
     *
     * @param connection connection
     * @param refresh    refrash data
     * @param logs       activate logs output
     * @return CP List of attributes
     */
    public CompletionStage<List<AttributeList>> getAttributeList(Connection connection, boolean refresh, boolean logs) {
        if (logs) {
            log.debug("getting AttributeList , start:{}", now());
        }

        String key = "attributes";

        if (refresh) {
            asyncCacheApi.remove(key);
        }

        return asyncCacheApi.getOrElseUpdate(key, () -> supplyAsync(() -> {

            List<AttributeList> attributeList = new ArrayList<>();

            try (PreparedStatement st = connection.prepareStatement(ATTRIBUTE_LIST);
                 ResultSet rs = st.executeQuery()) {

                while (rs.next()) {
                    attributeList.add(AttributeList.builder()
                            .groupingSetId(rs.getLong(1))
                            .sqlCalcName(rs.getString(2))
                            .build());
                }
            } catch (SQLException e) {
                log.error("getAttributeList query exec error:{} ", e.getMessage());
            }
            return attributeList;
        })).thenApply(res -> {
            if (logs) {
                log.debug("getting AttributeList , end:{}", now());
                log.debug("AttributeList size:{}", res.size());
            }
            return res;
        });
    }

}