package performance;

import akka.actor.ActorSystem;
import com.cloudera.impala.jdbc41.DataSource;
import com.typesafe.config.Config;
import controllers.RedisController;
import org.junit.Before;
import org.junit.Test;
import play.cache.AsyncCacheApi;
import play.test.WithApplication;
import scala.concurrent.ExecutionContext;

import java.sql.Connection;
import java.sql.SQLException;

import static utils.Constants.IMPALA_URL;

public class PerformanceTest extends WithApplication {

    private AsyncCacheApi asyncCacheApi;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;
    private Config config;

    @Before
    public void init() {
        this.asyncCacheApi = this.app.injector().instanceOf(AsyncCacheApi.class);
        this.actorSystem = this.app.injector().instanceOf(ActorSystem.class);
        this.executionContext = this.app.injector().instanceOf(ExecutionContext.class);
        this.config = this.app.injector().instanceOf(Config.class);
    }

    @Test
    public void attributesListTest() {

        RedisController controller = new RedisController(asyncCacheApi, actorSystem, executionContext, config);
        DataSource ds = new DataSource();
        ds.setURL(IMPALA_URL + config.getString("impala.host") + ":" + config.getString("impala.port"));
        try (Connection connection = ds.getConnection()) {
            for (int i = 1; i <= 50000; i++) {
                controller.getAttributeList(connection, false, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
