package utils.temporal.batis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Denis Danilin | denis@danilin.name
 * 02.10.2017 17:29
 * core-router ☭ sweat and blood
 */
@MappedTypes(ZonedDateTime.class)
public class ZonedTypeHandler extends BaseTypeHandler<ZonedDateTime> {

    // field must be varchar(25) at least

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final ZonedDateTime parameter, final JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Override
    public ZonedDateTime getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        return byPattern(rs.getString(columnName));
    }

    @Override
    public ZonedDateTime getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        return byPattern(rs.getString(columnIndex));
    }

    @Override
    public ZonedDateTime getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        return byPattern(cs.getString(columnIndex));
    }

    private ZonedDateTime byPattern(final Object value) {
        return value == null ? null : ZonedDateTime.parse(String.valueOf(value), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
