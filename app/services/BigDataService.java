package services;

import domain.bigdata.BigDataQueriesEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.BigDataRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class BigDataService {
    private static final Logger LOG = LoggerFactory.getLogger(BigDataService.class);

    private BigDataRepository repository;

    @Inject
    public BigDataService(BigDataRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Stream<BigDataQueriesEntity>> getAllQueries () {
        return repository.getAllQueries();
    }

    public BigDataQueriesEntity getQueryById (Long id) {
        return repository.getQueryById(id);
    }

    public BigDataQueriesEntity getQueryByKey (String key) {
        return repository.getQueryByKey(key);
    }

    public String insertQuery (String query) {
        return repository.insertQuery(query);
    }

}
