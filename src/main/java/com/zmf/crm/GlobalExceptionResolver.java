package com.zmf.crm;

import com.alibaba.fastjson.JSON;
import com.zmf.crm.base.ResultInfo;
import com.zmf.crm.exceptions.NoLoginException;
import com.zmf.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        /**
         * 判断异常类型
         * 如果是未登录异常，则先执⾏相关的拦截操作
         */
        if (e instanceof NoLoginException) {
            // 如果捕获的是未登录异常，则重定向到登录⻚⾯
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }
        ModelAndView mv = new ModelAndView();
        //设置默认的异常处理
        mv.setViewName("error");
        mv.addObject("code","400");
        mv.addObject("msg","系统异常，请稍后再试...");

        //判断HandlerMethod
        if (o instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) o;
            //获取方法上的ResponseBody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断ResponseBody注解是否存在 (如果不存在，表示返回的是视图;如果存在，表示返回的是JSON)
            if (null==responseBody){
                /*
                * 方法返回视图
                *
                * */
                if (e instanceof ParamsException){
                        ParamsException pe = (ParamsException) e;
                        mv.addObject("code",pe.getCode());
                        mv.addObject("msg",pe.getMsg());
                }
                return mv;
            }else {
                /*
                 * 方法上返回JSON
                 * */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试！");
                // 如果捕获的是⾃定义异常
                if (e instanceof ParamsException) {
                    ParamsException p = (ParamsException) e;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                httpServletResponse.setContentType("application/json;charset=utf-8");
                // 得到输出流
                PrintWriter out = null;
                try {
                    out = httpServletResponse.getWriter();
                    // 将对象转换成JSON格式，通过输出流输出
                    out.write(JSON.toJSONString(resultInfo));
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return null;
            }
        }

        return mv;
    }
}
