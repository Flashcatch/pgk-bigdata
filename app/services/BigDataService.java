package services;

import domain.bigdata.BigDataQueriesEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.BigDataRepository;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;
import javax.inject.Inject;

/**
 * BigDataService.
 * @author SandQ
 */
public class BigDataService {
    private static final Logger LOG = LoggerFactory.getLogger(BigDataService.class);

    private BigDataRepository repository;

    @Inject
    public BigDataService(BigDataRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Stream<BigDataQueriesEntity>> getAllQueries() {
        return repository.getAllQueries();
    }

    /**
     * getQueryById.
     * @param id id
     * @return BigDataQueriesEntity
     */
    public BigDataQueriesEntity getQueryById(Long id) {
        return repository.getQueryById(id);
    }

    /**
     * getQueryByKey.
     * @param key key
     * @return BigDataQueriesEntity
     */
    public BigDataQueriesEntity getQueryByKey(String key) {
        return repository.getQueryByKey(key);
    }

    /**
     * insertQuery.
     * @param query query
     * @return String
     */
    public String insertQuery(String query) {
        return repository.insertQuery(query);
    }

}
