package mappers;

import domain.bigdata.BigDataQueriesEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BigDataMapper interface.
 * @author SandQ
 */
public interface BigDataMapper {

    /**
     * select all queries.
     * @return list of BigDataQueriesEntity
     */
    List<BigDataQueriesEntity> selectAllQueries();

    /**
     * selectQueryById.
     * @param id id
     * @return BigDataQueriesEntity
     */
    BigDataQueriesEntity selectQueryById(@Param("id") Long id);

    /**
     * selectQueryByKey.
     * @param key key
     * @return BigDataQueriesEntity
     */
    BigDataQueriesEntity selectQueryByKey(@Param("key") String key);

    /**
     * insertQuery.
     * @param query query
     * @return String
     */
    String insertQuery(@Param("query") String query);

}
