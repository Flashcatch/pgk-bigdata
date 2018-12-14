package controllers;

import akka.actor.ActorSystem;
import com.cloudera.impala.jdbc41.DataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import domain.bigdata.*;
import dto.bigdata.BigDataQueryDto;
import dto.bigdata.BigDataQueryParamsDto;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import play.cache.AsyncCacheApi;
import play.cache.NamedCache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

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
import static utils.Constants.*;

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

    @Inject
    public RedisController(AsyncCacheApi asyncCacheApi,
                           ActorSystem actorSystem,
                           ExecutionContext executionContext,
                           Config config) {
        this.asyncCacheApi = asyncCacheApi;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.config = config;
    }

    /**
     * @return CompletionStageResult result
     */
    @ApiOperation(value = "Получение метрик из si_calculation, используя redis",
        consumes = "application/json",
        produces = "application/json")
    @ApiImplicitParams( {
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
    public CompletionStage<Result> getSiCalculationRedis() {
        log.debug("<< getSiCalculationRedis < start, now:{}", now());

        boolean logs = Boolean.parseBoolean(request().getQueryString("logs"));

        BigDataQueryResponseList response = new BigDataQueryResponseList();
        List<BigDataQueryResponse> responseList = new ArrayList<>();

        DataSource ds = new DataSource();
        ds.setURL(IMPALA_URL + config.getString("impala.host") + ":" + config.getString("impala.port"));

        try (Connection connection = ds.getConnection()) {

            // По ключу grsets получаем grouping_set_id, и уровень метрики
            List<GroupingSets> groupingSetsList = (List<GroupingSets>) asyncCacheApi.get("grsets")
                .toCompletableFuture().join();

            //log.debug("<< getSiCalculationRedis < Processing request data");
            JsonNode json = request().body().asJson();
            //log.debug("<< getSiCalculationRedis < json: {}", json);

            BigDataQueryDto body = Json.fromJson(json, BigDataQueryDto.class);
            //log.debug("<< getSiCalculationRedis < json parsed,body: {}", body);

            List<AttributeList> attributeList = getAttributeList(connection, false, logs)
                .toCompletableFuture().join();

            // Цикл по json данным
            List<BigDataQueryParamsDto> metricsBlanks = body.getMetricsBlanks();
            response.setActualDate(body.getActualDate());

            for (BigDataQueryParamsDto params : metricsBlanks) {

                long groupingSetId = -1;
                double duration = ABSENT_METRIX;
                String key;
                StringBuilder keyBuilder = new StringBuilder();

                Integer calcLvl = -1;
                for (GroupingSets groupingSets : groupingSetsList) {

                    groupingSetId = groupingSets.getGroupingSetId();

                    // фильтруем список атрибутов для текущего уровня
                    //TODO: проверять, если меняется уровень, не фильтровать каждый раз
                    final long grSetId = groupingSetId;
                    List<AttributeList> attrs = attributeList.stream()
                        .filter(data -> data.getGroupingSetId() == grSetId)
                        .collect(Collectors.toList());

                    if (logs) {
                        log.debug("groupingSetId:{} attributes:{}", groupingSetId, attrs.toString());

                    }
                    // Получаем станции и дороги
                    Long sndRwId = (Long) asyncCacheApi.get("station:rw:" + params.getSndStId()).toCompletableFuture().join();
                    Long sndDpId = (Long) asyncCacheApi.get("station:dp:" + params.getSndStId()).toCompletableFuture().join();
                    Long rsvRwId = (Long) asyncCacheApi.get("station:rw:" + params.getRsvStId()).toCompletableFuture().join();
                    Long rsvDpId = (Long) asyncCacheApi.get("station:dp:" + params.getRsvStId()).toCompletableFuture().join();

                    key = "sicalculation:grouping_set_id:" + groupingSetId + ":year_month:" + body.getActualDate();
                    keyBuilder = new StringBuilder(key);

                    for (AttributeList attrList : attrs) {

                        String sqlCalcName = attrList.getSqlCalcName();

                        if ("SND_RW_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":snd_rw_id:").append(sndRwId);
                        } else if ("RSV_RW_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":rsv_rw_id:").append(rsvRwId);
                        } else if ("SND_DP_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":snd_dp_id:").append(sndDpId);
                        } else if ("RSV_DP_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":rsv_dp_id:").append(rsvDpId);
                        } else if ("SND_DP_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":snd_dp_id:").append(params.getSndDpId());
                        } else if ("RSV_DP_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":rsv_dp_id:").append(params.getRsvDpId());
                        } else if ("SND_ST_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":snd_st_id:").append(params.getSndStId());
                        } else if ("RSV_ST_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":rsv_st_id:").append(params.getRsvStId());
                        } else if ("SND_ORG_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":snd_org_id:").append(params.getSndOrgId());
                        } else if ("RSV_ORG_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":rsv_org_id:").append(params.getRsvOrgId());
                        } else if ("FR_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":fr_id:").append(params.getFrId());
                        } else if ("ROD_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":rod_id:").append(params.getRodId());
                        } else if ("ROUTE_SEND_SIGN".equals(sqlCalcName)) {
                            keyBuilder.append(":route_send_sign:").append(params.getRouteSendSign());
                        } else if ("CLIENT_ID".equals(sqlCalcName)) {
                            keyBuilder.append(":client_id:").append(params.getClientId());
                        } else if ("VID_PODGOTOVKI".equals(sqlCalcName)) {
                            keyBuilder.append(":vid_podgotovki:").append(params.getVidPodgotovki());
                        } else if ("VID_ZABRAKOVKI".equals(sqlCalcName)) {
                            keyBuilder.append(":vid_zabrakovki:").append(params.getVidZabrakovki());
                        } else if ("IS_TECH_ST".equals(sqlCalcName)) {
                            keyBuilder.append(":is_tech_st:").append(params.getIsTechSt());
                        }

                    }

                    if (logs) {
                        log.debug("key={}", keyBuilder.toString());
                    }

                    // Проверяем json на полноту данных
                    duration = asyncCacheApi.get(keyBuilder.toString())
                        .toCompletableFuture().join() == null ? ABSENT_METRIX : (double) asyncCacheApi.get(keyBuilder.toString())
                        .toCompletableFuture().join();
                    calcLvl = groupingSets.getLevel();
                    if (duration > 0) break;

                }
                BigDataQueryResponse bdResponse = BigDataQueryResponse.builder()
                    .id(params.getId())
                    .duration(String.format("%1$,.2f", duration))
                    .sndStId(params.getSndStId())
                    .rsvStId(params.getRsvStId())
                    .rodId(params.getRodId())
                    .routeSendSign(params.getRouteSendSign())
                    .calcLevel(calcLvl)
                    .build();
                if (duration == ABSENT_METRIX) {
                    bdResponse.setDuration(String.format("%1$,.2f", ABSENT_METRIX));
                    bdResponse.setException("duration is null! key=" + keyBuilder.toString());
                }
                responseList.add(bdResponse);
            }

        } catch (SQLException e) {
            log.error("getStatIndicators sql error:{} ", e.getMessage());
        }
        response.setMetrics(responseList);
        log.debug("<< getSiCalculationRedis < end, now:{}", now());
        return completedFuture(ok(Json.toJson(response)));
    }

    private CompletionStage<List<GroupingSets>> getGroupingSets(Connection connection, boolean refresh) {
        log.debug("getting Grouping Sets, start:{}", now());

        String key = "grsets";

        if (refresh) {
            asyncCacheApi.remove(key);
        }

        return asyncCacheApi.getOrElseUpdate(key, () -> supplyAsync(() -> transferGroupingSets(connection))).thenApply(res -> {
            log.debug("getting Grouping Sets, end:{}", now());
            return res;
        });
    }

    private CompletionStage<List<Stations>> getStations(Connection connection, boolean refresh) {
        log.debug("getting stations from cache, start:{}", now());
        String key = "stations";

        if (refresh) {
            asyncCacheApi.remove(key);
        }

        log.debug("getting stations from cache, end:{}", now());
        return asyncCacheApi.getOrElseUpdate(key, () -> supplyAsync(() -> {
            log.debug("transferring stations to cache, start:{}", now());
            List<Stations> stations = new ArrayList<>();
            try (PreparedStatement st = connection.prepareStatement(STATIONS)) {
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next()) {
                    stations.add(Stations.builder()
                        .stationId(resultSet.getLong(1))
                        .rwId(resultSet.getLong(2))
                        .dpId(resultSet.getLong(3)).build());
                }
            } catch (SQLException e) {
                log.error("stations sql error:{} ", e.getMessage());
                asyncCacheApi.remove(key);
            }
            log.debug("transferring stations to cache, end:{}", now());
            return stations;
        })).thenApply(res -> {
            log.debug("getting stations, end:{}", now());
            log.debug("<< getSiCalculationRedis < stations populated, size:{}", res.size());
            return res;
        });
    }

    private CompletionStage<List<Freights>> getFreights(Connection connection, boolean refresh) {
        log.debug("getting freights from cache, start:{}", now());
        String key = "freights";

        if (refresh) {
            asyncCacheApi.remove(key);
        }

        log.debug("getting freights from cache, end:{}", now());
        return asyncCacheApi.getOrElseUpdate(key, () -> supplyAsync(() -> {
            log.debug("transferring freights to cache, start:{}", now());
            List<Freights> freights = new ArrayList<>();
            try (PreparedStatement st = connection.prepareStatement(FREIGHTS)) {
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next()) {
                    freights.add(Freights.builder()
                        .key(resultSet.getLong(1))
                        .group(resultSet.getLong(2))
                        .build());
                }
            } catch (SQLException e) {
                log.error("freights sql error:{} ", e.getMessage());
                asyncCacheApi.remove(key);
            }
            log.debug("transferring freights to cache, end:{}", now());
            return freights;
        })).thenApply(res -> {
            log.debug("getting freights, end:{}", now());
            log.debug("<< getSiCalculationRedis < freights populated, size:{}", res.size());
            return res;
        });
    }

    @SuppressWarnings("checkstyle:MagicNumberCheck")
    private CompletionStage<List<SiCalculation>> getSiCalculationRedis(Connection connection, boolean refresh, GroupingSets groupingSets) {
        log.debug("getting si_calculation with groupingSetId={}, start:{}", groupingSets, now());
        String key = "sicalculation" + groupingSets.getGroupingSetId();

        if (refresh) {
            asyncCacheApi.remove(key);
        }

        return asyncCacheApi.getOrElseUpdate(key, () -> supplyAsync(() -> {
            log.debug("transferring Si Calculation to cache, start:{}", now());
            List<SiCalculation> siCalculations = new ArrayList<>();
            try (PreparedStatement st = connection.prepareStatement(SICALCULATION_QUERY)) {
                st.setLong(1, groupingSets.getGroupingSetId());
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next()) {
                    siCalculations.add(SiCalculation.builder()
                        .groupingSetId(resultSet.getLong(1))
                        .yearMonth(resultSet.getString(2))
                        .sndCnId(resultSet.getLong(3))
                        .rsvCnId(resultSet.getLong(4))
                        .sndDpId(resultSet.getLong(5))
                        .rsvDpId(resultSet.getLong(6))
                        .sndRwId(resultSet.getLong(7))
                        .rsvRwId(resultSet.getLong(8))
                        .sndStId(resultSet.getLong(9))
                        .rsvStId(resultSet.getLong(10))
                        .sndOrgId(resultSet.getLong(11))
                        .rsvOrgId(resultSet.getLong(12))
                        .frId(resultSet.getLong(13))
                        .frGroupId(resultSet.getLong(14))
                        .isLoad(resultSet.getLong(15))
                        .rodId(resultSet.getLong(16))
                        .sendKindId(resultSet.getLong(17))
                        .parkSign(resultSet.getLong(18))
                        .routeSendSign(resultSet.getLong(19))
                        .modelPropertyId(resultSet.getLong(20))
                        .stId(resultSet.getLong(21))
                        .clientId(resultSet.getLong(22))
                        .vidPodgotovki(resultSet.getString(23))
                        .vidZabrakovki(resultSet.getLong(24))
                        .isTechSt(resultSet.getLong(25))
                        .aggMedian(resultSet.getFloat(26))
                        .build());
                }
            } catch (SQLException e) {
                log.error("getSiCalculationRedis sql error:{} ", e.getMessage());
                asyncCacheApi.remove(key);
            }
            log.debug("transferring SiCalculation to cache, end:{}", now());
            return siCalculations;
        })).thenApply(res -> {
            log.debug("getting si_calculation with groupingSetId={}, end:{}", groupingSets.getGroupingSetId(), now());
            log.debug("<< getSiCalculationRedis < SiCalculation populated, size:{}", res.size());
            return res;
        });
    }

    /**
     * @param key key
     * @return Result
     */
    @ApiOperation(value = "get")
    public CompletionStage<Result> get(@ApiParam(value = "key", required = true) String key) {
        CompletionStage<Object> result = asyncCacheApi.get(key);
        return result.thenApply(v -> {
            if (v instanceof ObjectNode) {
                return ok((JsonNode) v);
            } else if (v instanceof String) {
                return ok((String) v);
            } else {
                return ok(Json.toJson(v));
            }
        });

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
        if (value instanceof TextNode) {
            String resultString = ((TextNode) value).asText();
            return asyncCacheApi.set(key, resultString).thenApply(done -> created("Created"));
        } else if (value instanceof JsonNode) {
            JsonNode resultObject = value;
            return asyncCacheApi.set(key, resultObject).thenApply(done -> created("Created"));
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
     * Remove all data from redis
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
    @ApiImplicitParams( {
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