package modules;

import com.google.inject.name.Names;
import mappers.HomeMapper;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import play.db.Database;
import utils.LocalDateTimeHandler;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

public class BatisModule extends org.mybatis.guice.MyBatisModule {

    @Override
    protected void initialize() {
        environmentId("development");
        bindConstant().annotatedWith(
                Names.named("mybatis.configuration.failFast")).
                to(true);
        bindDataSourceProviderType(PlayDataSourceProvider.class); // это подключение датасорса, который заинжектен play-ем
        bindTransactionFactoryType(JdbcTransactionFactory.class);

        // Подключение пакета с мапперами
        addMapperClasses(HomeMapper.class.getPackage().getName());

        // Подключение отдельного маппера
//        addMapperClass(RateMapper.class);

        // подключение Temporal handler-ов
        addTypeHandlerClasses(LocalDateTimeHandler.class.getPackage().getName());
    }

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
