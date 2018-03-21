package com.zhongshu.vegetables.interceptor;

import com.alibaba.fastjson.JSON;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.encrypt.Algorithm;
import com.zhongshu.vegetables.encrypt.MessageDigestUtils;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.UserService;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class UserSecurityInterceptor implements HandlerInterceptor {

    //存放所有不需要拦截的接口
    private static List<String> pageList = new ArrayList<>();
    private UserService userService;

    static {
        pageList.add("login");
        pageList.add("register");
        pageList.add("getVerCode");
        pageList.add("checkVersion");
        pageList.add("getArea");
        pageList.add("searchKey");
        pageList.add("listener");
    }


    /**
     * 接口验证规则：
     * 从header中获取到appkey、timestamp、signature
     * 通过appkey获取用户信息（包括secret）
     * 判断SHA1(appkey_secret_timestamp)和signatrue是否一致，不一致则提示签名不正确，
     * 如果一致，则提取用户的权限，如果权限里面的地址包含了当前的地址，则允许通过，否则提示NO_AUTH
     * 为了安全考虑，强烈建议不要再客户端显示secret，一般通过登录，让服务器返回
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        //这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        //response.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String url = request.getRequestURL().toString();
        inject(request);

        if (!contains(pageList, url)) {
            //接口安全性验证规则：
            //1.登录后，服务器返回token
            //2.客户端拿到token后，生成签名
            //3.签名生成规则：参数升序排列进行sha1加密，生成签名
            //服务端获取参数，按照同样方式加密，生成签名，验证签名合法性
            //如果合法，则通过token获取用户信息
            String signatrue = request.getHeader("signatrue");
            String timestamp = request.getHeader("timestamp");
            String token = request.getHeader("token");
            User user = null;
            SingleResult<String> result = new SingleResult<>();

            if (StringUtils.isBlank(signatrue) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(token)) {
                result.setCode(Code.EXP_TOKEN);
                response.getWriter().print(JSON.toJSONString(result));
                return false;
            }
            Enumeration<String> params = request.getParameterNames();
            List<Map<String, Object>> paramList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            paramList.add(map);
            map = new HashMap<>();
            map.put("timestamp", timestamp);
            paramList.add(map);
            while (params.hasMoreElements()) {
                String name = params.nextElement();
                String value = request.getParameter(name);
                map = new HashMap<>();
                map.put(name, value);
                paramList.add(map);
            }
            //按照字母排序（升序）
            Collections.sort(paramList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return o1.keySet().iterator().next().compareTo(o2.keySet().iterator().next());
                }
            });
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Map<String, Object> item : paramList) {
                if (!first) {
                    builder.append('&');
                } else {
                    first = false;
                }
                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    builder.append(entry.getKey()).append('=').append(entry.getValue());
                }
            }

            user = userService.getUserByToken(token);
            if (null != user) {
                if (user.getEnable() == 1) {
                    request.setAttribute("user", user);
                    //保存日志 没写
                } else {
                    result.setCode(Code.DISABLED);
                    response.getWriter().print(JSON.toJSONString(result));
                    return false;
                }
            } else {
                result.setCode(Code.EXP_TOKEN);
                response.getWriter().print(JSON.toJSONString(result));
                return false;
            }


//            String sign = MessageDigestUtils.encrypt(builder.toString(), Algorithm.SHA1);
//            if (sign.equals(signatrue)) {
//                user = userService.getUserByToken(token);
//                if (null != user) {
//                    if (user.getEnable() == 1) {
//                        request.setAttribute("user", user);
//                        //保存日志 没写
//                    } else {
//                        result.setCode(Code.DISABLED);
//                        response.getWriter().print(JSON.toJSONString(result));
//                        return false;
//                    }
//                } else {
//                    result.setCode(Code.EXP_TOKEN);
//                    response.getWriter().print(JSON.toJSONString(result));
//                    return false;
//                }
//            } else {
//                result.setCode(Code.EXP_SIGNATURE);
//                response.getWriter().print(JSON.toJSONString(result));
//                return false;
//            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    /**
     * 是否包含指定字符串，模糊匹配
     *
     * @param list
     * @param value
     * @return
     */
    private boolean contains(List<String> list, String value) {
        if (null != list && list.size() > 0) {
            for (String str : list) {
                if (value.indexOf(str) > 0) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void inject(HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        if (null == userService) {
            userService = factory.getBean(UserService.class);
        }

    }

    /**
     * 获取IP
     *
     * @param request
     * @return
     * @throws Exception
     */
    private String getIpAddr(HttpServletRequest request) throws Exception {
        if (request == null) {
            return "";
        }
        String ipString = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ipString.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ipString = str;
                break;
            }
        }

        return ipString;
    }
}
