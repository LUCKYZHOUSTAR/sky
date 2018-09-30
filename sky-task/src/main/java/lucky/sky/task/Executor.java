package lucky.sky.task;

/**
 *
 */
@FunctionalInterface
public interface Executor {

  void execute(TaskContext ctx);
}
