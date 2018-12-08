package mappers;

import domain.bigdata.BigDataQueriesEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BigDataMapper {

    List<BigDataQueriesEntity> selectAllQueries();
    BigDataQueriesEntity selectQueryById(@Param("id") Long id);
    BigDataQueriesEntity selectQueryByKey(@Param("key") String key);
    String insertQuery(@Param("query") String query);

}
