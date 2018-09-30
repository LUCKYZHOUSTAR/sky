//package lucky.sky.web.test.controller;
//
//import lucky.sky.task.core.model.JobInfo;
//import lucky.sky.task.core.service.JobService;
//import lucky.sky.web.test.model.ReturnT;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * @Author:chaoqiang.zhou
// * @Description:
// * @Date:Create in 10:50 2018/3/22
// */
//@Controller
//@RequestMapping("job")
//public class JobController {
//
//
//  private JobService jobService;
//
//
//  @RequestMapping("/add")
//  @ResponseBody
//  public ReturnT<String> add(JobInfo jobInfo) {
//    jobService.addJob(jobInfo);
//    return ReturnT.SUCCESS;
//
//  }
//
//}
