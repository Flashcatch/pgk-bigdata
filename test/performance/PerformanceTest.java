package performance;

import akka.actor.ActorSystem;
import com.cloudera.impala.jdbc41.DataSource;
import controllers.RedisController;
import org.junit.Before;
import org.junit.Test;
import play.cache.AsyncCacheApi;
import play.test.WithApplication;
import scala.concurrent.ExecutionContext;

import java.sql.Connection;
import java.sql.SQLException;

public class PerformanceTest extends WithApplication {

    private AsyncCacheApi asyncCacheApi;
    private ActorSystem actorSystem;
    private ExecutionContext executionContext;

    @Before
    public void init() {
        this.asyncCacheApi = this.app.injector().instanceOf(AsyncCacheApi.class);
        this.actorSystem = this.app.injector().instanceOf(ActorSystem.class);
        this.executionContext = this.app.injector().instanceOf(ExecutionContext.class);
    }

    @Test
    public void attributesListTest() {

        RedisController controller = new RedisController(asyncCacheApi, actorSystem, executionContext);
        DataSource ds = new DataSource();
        ds.setURL("jdbc:impala://192.168.100.51:21050");
        try (Connection connection = ds.getConnection()) {
            for (int i = 1; i <= 50000; i++) {
                controller.getAttributeList(connection, false, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
