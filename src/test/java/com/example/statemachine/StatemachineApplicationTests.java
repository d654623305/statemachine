package com.example.statemachine;

import com.statemachine.*;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.UUID;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= WebApplication.class)
public class StatemachineApplicationTests {

    @Autowired
    private StateMachinePersister<States, Events, String> persister;

    @Resource
    private StateMachine<States, Events> stateMachine;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void Test() {
        //新建请假条
        BaseResponse result = new BaseResponse();
        stateMachine.start();
        LeaveRequest leave=new LeaveRequest();
        leave.setApplicant("张三");//申请人
        leave.setReason("生病了");//事由
        leave.setDate("20200620");//开始时间
        leave.setDays(3);//天数
        result.message = "新建请假申请成功";
        result.success = true;
        result.data = leave;
        logger.info("新建请假条："+ result.toString());
        String leaveId=leave.getLeaveId();
        //提交
        apply(leaveId);
        //待TL审批
        tlApprove(leaveId);
        //待DM审批
        dmApprove(leaveId);
        ////HR备案
        hrRecord(leaveId);
    }

    public void apply(String leaveId) {
        //提交
        //当前事件=SUBMIT 提交申请
        //提交一个请假单
        //提交对应状态=WAITING_FOR_TL_APPROVE 等待 TL 审批
        //提交对应事件=SUBMIT 提交申请
        BaseResponse result=sendEvent(Events.SUBMIT,leaveId);
        logger.info("提交请假单："+ result.toString());
    }


    public void tlApprove(String id) {
        //待TL审批
        //事件=TL审批通过 TL_AGREE; 状态= WAITING_FOR_TL_APPROVE 待TL审批
        //事件=TL审批拒绝 TL_REJECT；状态=WAITING_FOR_TL_APPROVE TL驳回

        boolean agree =true;
        BaseResponse result=sendEvent(agree ? Events.TL_AGREE : Events.TL_REJECT, id);
        logger.info("TL审批请假单："+ result.toString());
    }

    public void dmApprove(String id) {
        //待DM审批
        //事件=DM审批通过 DM_AGREE; 状态= WAITING_FOR_DM_APPROVE 部门经理审批
        //事件=DM审批拒绝 DM_REJECT；状态=WAITING_FOR_DM_APPROVE 部门经理驳回
        boolean agree = true;
        BaseResponse result=sendEvent(agree ? Events.DM_AGREE : Events.DM_REJECT, id);
        logger.info("DM审批请假单："+ result.toString());
    }

    public void hrRecord(String id) {
        //HR备案
        // 事件=HR_RECORD HR备案 ；状态=WAITING_FOR_HR_RECORD 备案

        BaseResponse result=sendEvent(Events.HR_RECORD,id);
        logger.info("HR备案："+ result.toString());
    }

    @Test
    public void getStateTest(){
        //获取状态机当前状态
        String leaveId ="afdf85c1-8fd0-4e91-b8cf-a86d71e9d4cb";
        BaseResponse result = new BaseResponse();

        try{
            persister.restore(stateMachine,leaveId);

            result.success = true;
            States state = stateMachine.getState().getId();

            result.data = state;

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            stateMachine.stop();
        }
        logger.info("当前状态是："+ result.toString());
    }

    private BaseResponse sendEvent(Events event, String leaveId){
        BaseResponse result = new BaseResponse();

        if(leaveId == null || leaveId.length()==0){
            result.success = false;
            result.message = "leaveId 不能为空";
            return result;
        }

        try {
            // 根据业务 id 获取状态
            persister.restore(stateMachine,leaveId);

            result.success = stateMachine.sendEvent(event);
            // 持久化状态机
            if (result.success) {
                persister.persist(stateMachine, leaveId);
            }
            JSONObject data = new JSONObject();

            result.message = result.success ? "执行成功":"执行失败";
            result.message = result.message + "，当前状态为："+stateMachine.getState().getId();
            data.put("leaveId",leaveId);
            data.put("event",event.toString());
            data.put("state",stateMachine.getState().getId());
            result.data = data;
        } catch (Exception e) {
            e.printStackTrace();
            result.message = e.getMessage();
        }finally {
            stateMachine.stop();
            return result;
        }
    }


}
