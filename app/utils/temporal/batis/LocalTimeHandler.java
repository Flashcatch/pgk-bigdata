package utils.temporal.batis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import utils.temporal.UConstants;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * @author Denis Danilin | denis@danilin.name
 * 02.10.2017 17:36
 * core-router ☭ sweat and blood
 */
@MappedTypes(LocalTime.class)
public class LocalTimeHandler extends BaseTypeHandler<LocalTime> {
    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final LocalTime parameter, final JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, Timestamp.from(parameter.atDate(LocalDate.of(1, 1, 1)).atZone(UConstants.utcZoneId).toInstant())
                , Calendar.getInstance(UConstants.utcTimeZone));
    }

    @Override
    public LocalTime getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        final Timestamp ts = rs.getTimestamp(columnName, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalTime.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }

    @Override
    public LocalTime getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        final Timestamp ts = rs.getTimestamp(columnIndex, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalTime.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }

    @Override
    public LocalTime getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        final Timestamp ts = cs.getTimestamp(columnIndex, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalTime.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }
}
