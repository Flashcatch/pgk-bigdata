package utils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Calendar;

@MappedTypes(LocalDateTime.class)
public class LocalDateTimeHandler extends BaseTypeHandler<LocalDateTime> {
    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final LocalDateTime parameter, final JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, Timestamp.from(parameter.atZone(UConstants.utcZoneId).toInstant()),
                Calendar.getInstance(UConstants.utcTimeZone));
    }

    @Override
    public LocalDateTime getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        final Timestamp ts = rs.getTimestamp(columnName, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalDateTime.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }

    @Override
    public LocalDateTime getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        final Timestamp ts = rs.getTimestamp(columnIndex, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalDateTime.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }

    @Override
    public LocalDateTime getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        final Timestamp ts = cs.getTimestamp(columnIndex, Calendar.getInstance(UConstants.utcTimeZone));

        if (ts == null)
            return null;

        return LocalDateTime.from(ts.toInstant().atZone(UConstants.utcZoneId));
    }
}
