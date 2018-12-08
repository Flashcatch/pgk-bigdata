package utils;

/**
 * Constants.
 *
 * @author Sand
 */
public final class Constants {

    public static final Long WAGON_NUM = 60_037_652L;
    public static final Long ABSENT_METRIX = -100L;
    public static final int NTH_LINE = 500000;

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String KEYCLOAK_CHECK_TOKEN = "keycloak.api.token.check";

    public static final String START_OF_TIME = "2000-01-01T00:00:00";
    public static final String END_OF_TIME = "2100-01-01T00:00:00";

    public static final String LKK_HOME_URL = "https://f3.pgk.arealidea.ru/api/v1/auth/mreporter/";

    public static final String TABLE_PREFIX = "ws_metrix.";

    public static final String GROUPING_SET = "select g.stat_indicator_id, g.grouping_set_id, g.level_val from " +
        TABLE_PREFIX + "si_stat_indicator i, " + TABLE_PREFIX + "si_grouping_set g" +
        " where i.stat_indicator_id = g.stat_indicator_id " +
        " ORDER BY stat_indicator_id asc, LEVEL_VAL desc";

    public static final String ATTRIBUTE_LIST = "select gs.grouping_set_id" +
        "     , al.sql_calc_name" +
        "  from " + TABLE_PREFIX + "si_grouping_set gs" +
        "     , " + TABLE_PREFIX + "si_grouping_set_elem gse     " +
        "     , " + TABLE_PREFIX + "attribute_list al     " +
        " where gse.grouping_set_id = gs.grouping_set_id" +
        "   and al.attribute_list_id = gse.attribute_list_id";

    public static final String SICALCULATION_QUERY = "select grouping_set_id," +
            "  year_month," +
            "  snd_cn_id ," +
            "  rsv_cn_id ," +
            "  snd_dp_id ," +
            "  rsv_dp_id ," +
            "  snd_rw_id ," +
            "  rsv_rw_id ," +
            "  snd_st_id ," +
            "  rsv_st_id ," +
            "  snd_org_id ," +
            "  rsv_org_id ," +
            "  fr_id ," +
            "  fr_group_id ," +
            "  isload ," +
            "  rod_id ," +
            "  send_kind_id ," +
            "  park_sign ," +
            "  route_send_sign ," +
            "  model_property_id ," +
            "  st_id ," +
            "  client_id ," +
            "  vid_podgotovki ," +
            "  vid_zabrakovki ," +
            "  is_tech_st ," +
            "  agg_median" +
            "  from " + TABLE_PREFIX + "si_calculation c";

    public static final String STATIONS = "select s.station_id" +
        "     , s.rw_id" +
        "     , s.dp_id" +
        "  from " + TABLE_PREFIX + "station s" +
        " where now() between s.beg_date and s.end_date";

    public static final String FREIGHTS = "select f.key, f.fr_group" +
        "  from " + TABLE_PREFIX + "freight f";


    /*"select st.DP_ID from ml.ws_station st"
            + " where st.station_id = ?"
            + "   and sysdate between st.beg_date and st.end_date "
            + "UNION"
            + "select st.DP_ID"
            + "  from ml.ws_station st"
            + " where st.station_id = ?"
            + "   and sysdate between st.beg_date and st.end_date "
            + "UNION"
            + "select st.RW_ID "
            + "  from ml.ws_station st"
            + " where st.station_id = ?"
            + "   and sysdate between st.beg_date and st.end_date "
            + "UNION "
            + "select st.RW_ID"
            + "  from ml.ws_station st"
            + " where st.station_id = ?"
            + "   and sysdate between st.beg_date and st.end_date";*/

    private Constants() {
    }
}
