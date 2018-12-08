package repository;

import domain.bigdata.BigDataQueriesEntity;
import mappers.BigDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;
import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * BigDataRepository.
 * @author SandQ
 */
public class BigDataRepository {

    private static final Logger LOG = LoggerFactory.getLogger(BigDataRepository.class);

    private final BigDataMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public BigDataRepository(BigDataMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Stream<BigDataQueriesEntity>> getAllQueries() {
        return supplyAsync(() -> {
            LOG.debug(">> getAllQueries");

            List<BigDataQueriesEntity> bigDataQueriesEntities = mapper.selectAllQueries();
            LOG.debug(">> getAllQueries > selectAllQueries");

            return bigDataQueriesEntities.stream();
        }, ec);
    }

    /**
     * getQueryById.
     * @param id id
     * @return BigDataQueriesEntity
     */
    public BigDataQueriesEntity getQueryById(Long id) {
        LOG.debug(">> getQueryById");

        BigDataQueriesEntity bigDataQueriesEntity = mapper.selectQueryById(id);
        LOG.debug(">> getQueryById > selectQueryById: {}", id);

        return bigDataQueriesEntity;
    }

    /**
     * getQueryByKey.
     * @param key key
     * @return BigDataQueriesEntity
     */
    public BigDataQueriesEntity getQueryByKey(String key) {
        LOG.debug(">> getQueryByKey");

        BigDataQueriesEntity bigDataQueriesEntity = mapper.selectQueryByKey(key);
        LOG.debug(">> getQueryByKey > selectQueryByKey: {}", key);

        return bigDataQueriesEntity;
    }

    /**
     * insertQuery.
     * @param query query
     * @return String
     */
    public String insertQuery(String query) {
        LOG.debug(">> insertQuery");

        String key = mapper.insertQuery(query);
        LOG.debug(">> insertQuery > insertQuery: {}", query);

        return key;
    }

}
