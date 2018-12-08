package controllers;

import akka.actor.ActorSystem;
import com.cloudera.impala.jdbc41.DataSource;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.client.*;
import play.cache.AsyncCacheApi;
import play.cache.NamedCache;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import services.BigDataService;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 *
 * @author SandQ
 */

@Slf4j
@Api(value = "BigData контроллер", produces = "application/json")
public class BigDataController extends Controller {

    @NamedCache("local")
    private final AsyncCacheApi asyncCacheApi;

    private final BigDataService bigDataService;

    private final Database db;

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    @Inject
    public BigDataController(AsyncCacheApi asyncCacheApi,
                             BigDataService bigDataService,
                             @NamedDatabase("default") Database db,
                             ActorSystem actorSystem,
                             ExecutionContext executionContext) {
        this.asyncCacheApi = asyncCacheApi;
        this.bigDataService = bigDataService;
        this.db = db;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
    }

    @ApiOperation(value = "Получение Big Data запросов")
    public CompletionStage<Result> getBigDataQueries() {

        return bigDataService.getAllQueries()
            .thenApply(res -> ok(Json.toJson(res.collect(Collectors.toList()))));
    }

    /**
     * @param id identificator
     * @return Result
     */
    @ApiOperation(value = "Получение Big Data запроса по id")
    public Result getBigDataQueryById(Long id) {

        return ok(Json.toJson(bigDataService.getQueryById(id)));

    }

    /**
     * @param key key
     * @return Result
     */
    @ApiOperation(value = "Получение Big Data запроса по ключу")
    public Result getBigDataQueryByKey(String key) {

        return ok(Json.toJson(bigDataService.getQueryByKey(key)));
    }

    /**
     * @return Result
     */
    @ApiOperation(value = "Получить данные по таблице из kudu, если параметр tableName пустой, выводит список всех таблиц kudu")
    @ApiImplicitParams( {
        @ApiImplicitParam(
            name = "tableName",
            dataType = "string",
            paramType = "query",
            defaultValue = "impala::ws_metrix.rod",
            value = "Имя таблицы в kudu"
        ),
        @ApiImplicitParam(
            name = "rowCount",
            dataType = "int",
            paramType = "query",
            defaultValue = "1",
            value = "Количество строк из таблицы"
        )
    })
    public Result kuduDirect() {

        String tableName = request().getQueryString("tableName");
        int rowcount = Integer.parseInt(request().getQueryString("rowCount"));

        KuduClient client = new KuduClient.KuduClientBuilder("dwh-db1.domain.local:7051").build();

        if (tableName == null) {

            // Get all table names from kudu
            ListTablesResponse tables = null;
            try {
                tables = client.getTablesList();
            } catch (KuduException e) {
                return null;
            }
            tables.getTablesList().forEach(table -> log.debug("table: {}", table));
            return ok(Json.toJson(tables.getTablesList()));

        } else {

            KuduTable table = null; // "impala::ws_metrix.rod"
            try {
                table = client.openTable(tableName);
            } catch (KuduException e) {
                return null;
            }
            Schema schema = table.getSchema();

            Map<String, List<String>> dataMap = new HashMap<>();

            //Creating list of projecting column names, ggg
            List<String> tableColumns = new ArrayList<>();
            for (ColumnSchema columnSchema : schema.getColumns()) {
                tableColumns.add(columnSchema.getName());
                log.debug("got table {} column name: {} of {}", tableName, columnSchema.getName(), columnSchema.getType());
                dataMap.put(columnSchema.getName(), new ArrayList<>());
            }

            KuduScanner scanner = client.newScannerBuilder(table)
                .setProjectedColumnNames(tableColumns)
                .build();

            int rowNum = 0;
            while (scanner.hasMoreRows()) {

                RowResultIterator results = null;
                try {
                    results = scanner.nextRows();
                } catch (KuduException e) {
                    return null;
                }

                while (results.hasNext()) {

                    // Взяли немного и хватит для начала
                    if (rowNum == rowcount) return ok(Json.toJson(dataMap));

                    RowResult result = results.next();
                    for (ColumnSchema columnSchema : schema.getColumns()) {
                        switch (columnSchema.getType().getName().toLowerCase()) {
                            case "int64":
                                try {
                                    dataMap.get(columnSchema.getName()).add(Long.valueOf(result.getLong(columnSchema.getName())) == null ?
                                        String.valueOf(result.getLong(columnSchema.getName())) : "");
                                } catch (IllegalArgumentException e) {
                                    // Data is null, ne dobavljaem
                                }
                                break;
                            default:
                                try {
                                    dataMap.get(columnSchema.getName()).add(result.getString(columnSchema.getName()));
                                } catch (IllegalArgumentException e) {
                                    // Data is null, ne dobavljaem
                                }
                                break;
                        }
                    }
                    rowNum++;
                }

            }
            return ok(Json.toJson(dataMap));
        }
    }

    /**
     * @return CP Result
     */
    @ApiOperation(value = "Получить данные по таблице из kudu, если параметр tableName пустой, выводит список всех таблиц kudu")
    @ApiImplicitParams( {
        @ApiImplicitParam(
            name = "tableName",
            dataType = "string",
            paramType = "query",
            defaultValue = "impala::ws_metrix.rod",
            value = "Имя таблицы в kudu"
        ),
        @ApiImplicitParam(
            name = "rowCount",
            dataType = "int",
            paramType = "query",
            defaultValue = "1",
            value = "Количество строк из таблицы"
        )

    })
    public CompletionStage<Result> kuduCached() {

        String tableName = request().getQueryString("tableName");
        int rowcount = Integer.parseInt(request().getQueryString("rowCount"));

        return asyncCacheApi.getOrElseUpdate(tableName, () -> supplyAsync(() -> {
                log.debug("> kuduCached >> cache not found");
                KuduClient client = new KuduClient.KuduClientBuilder("dwh-db1.domain.local:7051").build();

                if (tableName == null) {

                    // Get all table names from kudu
                    ListTablesResponse tables = null;
                    try {
                        tables = client.getTablesList();
                    } catch (KuduException e) {
                        return null;
                    }
                    tables.getTablesList().forEach(table -> log.debug("table: {}", table));
                    return ok(Json.toJson(tables.getTablesList()));

                } else {

                    KuduTable table = null; // "impala::ws_metrix.rod"
                    try {
                        table = client.openTable(tableName);
                    } catch (KuduException e) {
                        return null;
                    }
                    Schema schema = table.getSchema();

                    Map<String, List<String>> dataMap = new HashMap<>();

                    //Creating list of projecting column names, ggg
                    List<String> tableColumns = new ArrayList<>();
                    for (ColumnSchema columnSchema : schema.getColumns()) {
                        tableColumns.add(columnSchema.getName());
                        log.debug("got table {} column name: {} of {}", tableName, columnSchema.getName(), columnSchema.getType());
                        dataMap.put(columnSchema.getName(), new ArrayList<>());
                    }

                    KuduScanner scanner = client.newScannerBuilder(table)
                        .setProjectedColumnNames(tableColumns)
                        .build();

                    int rowNum = 0;
                    while (scanner.hasMoreRows()) {

                        RowResultIterator results = null;
                        try {
                            results = scanner.nextRows();
                        } catch (KuduException e) {
                            return null;
                        }

                        while (results.hasNext()) {

                            // Взяли немного и хватит для начала
                            if (rowNum == rowcount) return ok(Json.toJson(dataMap));

                            RowResult result = results.next();
                            for (ColumnSchema columnSchema : schema.getColumns()) {
                                switch (columnSchema.getType().getName().toLowerCase()) {
                                    case "int64":
                                        try {
                                            dataMap.get(columnSchema.getName()).add(Long.valueOf(result.getLong(columnSchema.getName())) == null ? String.valueOf(result.getLong(columnSchema.getName())) : "");
                                        } catch (IllegalArgumentException e) {
                                            // Data is null, ne dobavljaem
                                        }
                                        break;
                                    default:
                                        try {
                                            dataMap.get(columnSchema.getName()).add(result.getString(columnSchema.getName()));
                                        } catch (IllegalArgumentException e) {
                                            // Data is null, ne dobavljaem
                                        }
                                        break;
                                }
                            }
                            rowNum++;
                        }

                    }
                    return ok(Json.toJson(dataMap));
                }
            })
        );
    }

    @ApiOperation(value = "Перегрузить данные из таблиц kudu в postgreSQL")
    public Result kuduToPostgreSQL() {

        List<String> tables = new ArrayList<>();
        tables.add("attribute_list");
        tables.add("freight");
        tables.add("si_grouping_set");
        tables.add("si_grouping_set_elem");
        tables.add("si_stat_indicator");
        tables.add("station");
        tables.add("si_calculation");

        //int rowcount = Integer.valueOf(request().getQueryString("rowCount"));

        this.actorSystem.scheduler().scheduleOnce(
            Duration.create(10, TimeUnit.SECONDS), // delay
            () -> {
                // Очищаем таблицы перед обновлением
                log.debug(">> kuduToPostgreSQL > cleaning tables");
                for (String tableName : tables) {
                    log.debug(">> kuduToPostgreSQL > clean table:{}", tableName);
                    try (Connection pgConnection = db.getConnection()) {
                        CallableStatement stmt = pgConnection.prepareCall("truncate table " + tableName);
                        stmt.execute();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    log.debug(">> kuduToPostgreSQL > Done cleaning.");

                    DataSource ds = new com.cloudera.impala.jdbc41.DataSource();
                    ds.setURL("jdbc:impala://192.168.100.51:21050");
                    long counter = 0;

                    String query;
                    query = "select * from ws_metrix." + tableName;

                    log.debug(">> kuduToPostgreSQL > processing table:{} query: {}", tableName, query);

                    try (Connection kuduConnection = ds.getConnection();
                         Statement statement = kuduConnection.createStatement();
                         ResultSet resultSet = statement.executeQuery(query)) {

                        try (Connection pgConnection = db.getConnection()) {
                            CallableStatement stmt;

                            while (resultSet.next()) {

                                if ("freight".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into freight(key, name, class, \"group\", etsng, gng, etsng6) " +
                                        "values(?, ?, ?, ?, ?, ?, ?)");
                                    stmt.setInt(1, resultSet.getInt("key"));
                                    stmt.setString(2, resultSet.getString("name"));
                                    stmt.setString(3, resultSet.getString("fr_class"));
                                    stmt.setInt(4, resultSet.getInt("fr_group"));
                                    stmt.setString(5, resultSet.getString("etsng"));
                                    stmt.setString(6, resultSet.getString("gng"));
                                    stmt.setString(7, resultSet.getString("etsng6"));
                                    stmt.execute();
                                } else if ("station".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into station(station_id, name, esr6, rw_id, cn_id, dp_id, beg_date, end_date) " +
                                        "values(?, ?, ?, ?, ?, ?, ?, ?)");
                                    stmt.setInt(1, resultSet.getInt("station_id"));
                                    stmt.setString(2, resultSet.getString("name"));
                                    stmt.setString(3, resultSet.getString("esr6"));
                                    stmt.setInt(4, resultSet.getInt("rw_id"));
                                    stmt.setInt(5, resultSet.getInt("cn_id"));
                                    stmt.setInt(6, resultSet.getInt("dp_id"));
                                    stmt.setTimestamp(7, resultSet.getTimestamp("beg_date"));
                                    stmt.setTimestamp(8, resultSet.getTimestamp("end_date"));
                                    stmt.execute();
                                } else if ("si_stat_indicator".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into si_stat_indicator(stat_indicator_id, mnemo, note) " +
                                        "values(?, ?, ?)");
                                    stmt.setInt(1, resultSet.getInt("stat_indicator_id"));
                                    stmt.setString(2, resultSet.getString("mnemo"));
                                    stmt.setString(3, resultSet.getString("note"));
                                    stmt.execute();
                                } else if ("si_grouping_set_elem".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into si_grouping_set_elem(grouping_set_id, attribute_list_id) " +
                                        "values(?, ?)");
                                    stmt.setInt(1, resultSet.getInt("grouping_set_id"));
                                    stmt.setInt(2, resultSet.getInt("attribute_list_id"));
                                    stmt.execute();
                                } else if ("si_grouping_set".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into si_grouping_set(grouping_set_id,stat_indicator_id, str, level_val, sql_select ) " +
                                        "values(?, ?, ? ,?, ?)");
                                    stmt.setInt(1, resultSet.getInt("grouping_set_id"));
                                    stmt.setInt(2, resultSet.getInt("stat_indicator_id"));
                                    stmt.setString(3, resultSet.getString("str"));
                                    stmt.setInt(4, resultSet.getInt("level_val"));
                                    stmt.setString(5, resultSet.getString("sql_select"));
                                    stmt.execute();
                                } else if ("attribute_list".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into attribute_list(attribute_list_id, mnemo, sql_calc_name) " +
                                        "values(?, ?, ?)");
                                    stmt.setInt(1, resultSet.getInt("attribute_list_id"));
                                    stmt.setString(2, resultSet.getString("mnemo"));
                                    stmt.setString(3, resultSet.getString("sql_calc_name"));
                                    stmt.execute();
                                } else if ("si_calculation".equals(tableName)) {
                                    stmt = pgConnection.prepareCall("insert into si_calculation(grouping_set_id,year_month,snd_cn_id,rsv_cn_id,snd_dp_id,rsv_dp_id,snd_rw_id,rsv_rw_id,snd_st_id,rsv_st_id,snd_org_id,rsv_org_id,fr_id,fr_group_id,isload,rod_id,send_kind_id,park_sign,route_send_sign,model_property_id,st_id,client_id,vid_podgotovki,vid_zabrakovki,is_tech_st,agg_median) " +
                                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?)");
                                    stmt.setInt(1, resultSet.getInt("grouping_set_id"));
                                    stmt.setString(2, resultSet.getString("year_month"));
                                    stmt.setInt(3, resultSet.getInt("snd_cn_id"));
                                    stmt.setInt(4, resultSet.getInt("rsv_cn_id"));
                                    stmt.setInt(5, resultSet.getInt("snd_dp_id"));
                                    stmt.setInt(6, resultSet.getInt("rsv_dp_id"));
                                    stmt.setInt(7, resultSet.getInt("snd_rw_id"));
                                    stmt.setInt(8, resultSet.getInt("rsv_rw_id"));
                                    stmt.setInt(9, resultSet.getInt("snd_st_id"));
                                    stmt.setInt(10, resultSet.getInt("rsv_st_id"));
                                    stmt.setInt(11, resultSet.getInt("snd_org_id"));
                                    stmt.setInt(12, resultSet.getInt("rsv_org_id"));
                                    stmt.setInt(13, resultSet.getInt("fr_id"));
                                    stmt.setInt(14, resultSet.getInt("fr_group_id"));
                                    stmt.setInt(15, resultSet.getInt("isload"));
                                    stmt.setInt(16, resultSet.getInt("rod_id"));
                                    stmt.setInt(17, resultSet.getInt("send_kind_id"));
                                    stmt.setInt(18, resultSet.getInt("park_sign"));
                                    stmt.setInt(19, resultSet.getInt("route_send_sign"));
                                    stmt.setInt(20, resultSet.getInt("model_property_id"));
                                    stmt.setInt(21, resultSet.getInt("st_id"));
                                    stmt.setInt(22, resultSet.getInt("client_id"));
                                    stmt.setString(23, resultSet.getString("vid_podgotovki"));
                                    stmt.setInt(24, resultSet.getInt("vid_zabrakovki"));
                                    stmt.setInt(25, resultSet.getInt("is_tech_st"));
                                    stmt.setInt(26, resultSet.getInt("agg_median"));
                                    stmt.execute();
                                }
                            }

                        } catch (SQLException e) {
                            log.debug("SqlException:{}", e.getMessage());
                        }

                        log.debug(">> kuduToPostgreSQL > processing table:{} ended!", tableName, query);

                    } catch (SQLException e) {
                        log.debug("SqlException:{}", e.getMessage());
                    }
                }

                log.debug(">> kuduToPostgreSQL > processing complete");

            },
            this.executionContext);

        return ok("reload job started");
    }

}