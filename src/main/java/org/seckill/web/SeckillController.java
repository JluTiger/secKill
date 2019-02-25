package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.dto.SecKillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SecKillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.service.SecKillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 表现层
 */
@Controller
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //获取列表页
        List<Seckill> list = secKillService.getSecKillList();
        model.addAttribute("list", list);
        // list.jsp + model = ModelAndView
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = secKillService.getById(seckillId);
        if (seckill == null) {
            return "foward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);

        return "detail";
    }

    //ajax json

    @RequestMapping(value = "/seckill/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SecKillResult<Exposer> result;
        try {
            Exposer exposer = secKillService.exportSecKillUrl(seckillId);
            result = new SecKillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SecKillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/seckill/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<SecKillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {

        if (phone == null) {
            return new SecKillResult<SecKillExecution>(false, "未注册");
        }
        try {
            //优化后，使用存储过程
            SecKillExecution execution = secKillService.excuteSecKillProcedure(seckillId,phone,md5);
            return new SecKillResult<SecKillExecution>(true, execution);
        } catch (SecKillCloseException e) {
            SecKillExecution execution = new SecKillExecution(seckillId, SecKillStatEnum.END);
            return new SecKillResult<SecKillExecution>(true, execution);
        } catch (RepeatKillException e) {
            SecKillExecution execution = new SecKillExecution(seckillId, SecKillStatEnum.REPEAT_KILL);
            return new SecKillResult<SecKillExecution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SecKillExecution execution = new SecKillExecution(seckillId, SecKillStatEnum.INNER_ERROR);
            return new SecKillResult<SecKillExecution>(true, execution);
        }
    }


    @RequestMapping(value = "/seckill/time/now", method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<Long> time() {
        Date now = new Date();
        return new SecKillResult<Long>(true, now.getTime());
    }

}

