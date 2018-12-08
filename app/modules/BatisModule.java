package modules;

import com.google.inject.name.Names;
import mappers.BigDataMapper;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import play.db.Database;
import utils.LocalDateTimeHandler;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

/**
 * Module for MyBatis DB Operations.
 * @author SandQ
 */
public class BatisModule extends org.mybatis.guice.MyBatisModule {

    @Override
    protected void initialize() {
        environmentId("development");
        bindConstant().annotatedWith(
            Names.named("mybatis.configuration.failFast")).to(true);
        bindDataSourceProviderType(PlayDataSourceProvider.class); // это подключение датасорса, который заинжектен play-ем
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        addMapperClass(BigDataMapper.class);

        // подключение Temporal handler-ов
        addTypeHandlerClasses(LocalDateTimeHandler.class.getPackage().getName());
    }

    /**
     *  Data source Provider.
     */
    @Singleton
    public static class PlayDataSourceProvider implements Provider<DataSource> {
        final Database db;

        // сюда инжектится датасорс из конфига
        @Inject
        public PlayDataSourceProvider(final Database db) {
            this.db = db;
        }

        @Override
        public DataSource get() {
            return db.getDataSource();
        }
    }

}
