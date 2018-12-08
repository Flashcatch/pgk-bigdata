package repository;

import domain.bigdata.BigDataQueriesEntity;
import mappers.BigDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

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

    public BigDataQueriesEntity getQueryById(Long id) {
            LOG.debug(">> getQueryById");

            BigDataQueriesEntity bigDataQueriesEntity = mapper.selectQueryById(id);
            LOG.debug(">> getQueryById > selectQueryById: {}", id);

            return bigDataQueriesEntity;
    }

    public BigDataQueriesEntity getQueryByKey(String key) {
        LOG.debug(">> getQueryByKey");

        BigDataQueriesEntity bigDataQueriesEntity = mapper.selectQueryByKey(key);
        LOG.debug(">> getQueryByKey > selectQueryByKey: {}", key);

        return bigDataQueriesEntity;
    }

    public String insertQuery(String query) {
        LOG.debug(">> insertQuery");

        String key = mapper.insertQuery(query);
        LOG.debug(">> insertQuery > insertQuery: {}", query);

        return key;
    }

}
