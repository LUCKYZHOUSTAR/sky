package lucky.sky.task.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
public class NotifyRequest {

  private String id;

  private String name;

  private Result result;

  private LocalDateTime startTime;

  private LocalDateTime endTime;
  private String ip;
}
