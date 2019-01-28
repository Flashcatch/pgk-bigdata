package utils;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

/**
 * Custom execution context wired to "event.dao" thread pool.
 * @author SandQ
 */
public class SiCalcExecutionContext extends CustomExecutionContext {
    @Inject
    public SiCalcExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "si-calc-ec");
    }
}
