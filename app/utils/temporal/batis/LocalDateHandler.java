package utils.temporal.batis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import utils.temporal.UConstants;

import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * @author Denis Danilin | denis@danilin.name
 * 15.09.2017 14:44
 * core-router ☭ sweat and blood
 */
@MappedTypes(LocalDate.class)
public class LocalDateHandler extends BaseTypeHandler<LocalDate> {
    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final LocalDate parameter, final JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, Timestamp.from(parameter.atStartOfDay().atZone(UConstants.utcZoneId).toInstant()),
                Calendar.getInstance(UConstants.utcTimeZone));
    }

    @Override
    public LocalDate getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        final Timestamp ts = rs.getTimestamp(columnName, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalDate.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }

    @Override
    public LocalDate getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        final Timestamp ts = rs.getTimestamp(columnIndex, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalDate.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }

    @Override
    public LocalDate getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        final Timestamp ts = cs.getTimestamp(columnIndex, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalDate.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }
}
