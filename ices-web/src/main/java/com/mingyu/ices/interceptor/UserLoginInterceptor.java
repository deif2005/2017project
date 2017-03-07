package com.mingyu.ices.interceptor;

import com.mingyu.ices.constant.WebConstants;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * 用户登陆会话拦截器
 * User: yuhao
 * Date: 2016-06-29
 */
public class UserLoginInterceptor implements HandlerInterceptor {

	private List<String> excludedUrls;

	public void setExcludedUrls(List<String> excludedUrls) {
		this.excludedUrls = excludedUrls;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//忽略不过滤的url
		String requestUri = request.getRequestURI();
		for (String url : excludedUrls) {
			if (requestUri.endsWith(url)) {
				return true;
			}
		}

		HttpSession session = request.getSession();
		//判断Session是否失效，判断登陆时放入session的用户信息是否为空
		if (session.getAttribute("USER_INFO") == null) {
			//异步请求处理，返回code，公共js弹出提示跳转到登陆页面
			if(request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){//ajax
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"rtnCode\":" + WebConstants.SESSION_INVALID + "}");
				out.flush();
				out.close();
			}
			//同步请求直接重定向到登陆页面
			else {
				//弹出提示并跳转到登陆页面
				StringBuffer builder = new StringBuffer();
				builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
				builder.append("alert('您太久没有操作了，请重新登陆！');");
				builder.append("window.top.location.href='/';");
				builder.append("</script>");
				html(response,builder.toString());
			}
			return false;
		}
		//如果session没有失效
		else {
			return true;
		}
	}

	/**
	 * 执行html js 代码
	 *
	 * @param response
	 * @param html
	 * @throws IOException
	 */
	protected void html(HttpServletResponse response, String html)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(html);
		out.close();
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
}
