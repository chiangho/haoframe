package hao.framework.test;

import java.util.Map;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

/** 
 * @author chianghao
 */
//这个必须使用junit4.9以上才有
@RunWith(SpringJUnit4ClassRunner.class)
//单元测试的时候真实的开启一个web服务
@WebAppConfiguration
//配置事务的回滚,对数据库的增删改都会回滚,便于测试用例的循环利用
//{"classpath:spring.xml","classpath:spring-hibernate.xml"}
@ContextConfiguration(locations ={"classpath:spring-context.xml","classpath:spring-mvc.xml"})
public class AbstractContextControllerTest {
	
	@Autowired
	protected WebApplicationContext wac;
	
	protected MockMvc mockMvc;
	
	@Before
    public void setUp() throws Exception {
		//mockMvc = MockMvcBuilders.standaloneSetup(new TestAction()).build();
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter((Filter)wac.getBean("shiroFilter")).build(); 
		
    }
//	 String responseString = mockMvc.perform(
//			 MockMvcRequestBuilders.post("/test.action")    //请求的url,请求的方法是get
//                       // .contentType(MediaType.APPLICATION_JSON)  //数据的格式
//                       // .param("id","123456789")         //添加参数
//        ).andExpect(MockMvcResultMatchers.status().isOk())    //返回的状态是200
//         .andDo(MockMvcResultHandlers.print())         //打印出请求和相应的内容
//         .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
	/**
	 * perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
	 * get:声明发送一个get请求的方法。MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables)：根据uri模板		和uri变量值得到一个GET请求方式的。另外提供了其他的请求的方法，如：post、put、delete等。
	 * param：添加request的参数，如上面发送请求的时候带上了了pcode = root的参数。假如使用需要发送json数据格式的时将不能使用这种		方式，可见后面被@ResponseBody注解参数的解决方法
	 * andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确（对返回的数据进行的判断）；
	 * andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台（对返回的数据进行的判断）；
	 * andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理（对返回的数据进行的判断）
	 * @throws Exception
	 */
	protected String request(String uri,Map<String,String> params) {
		MultiValueMap<String, String> _params = new LinkedMultiValueMap<String, String>();
		if(params!=null) {
			for(String key:params.keySet()) {
				_params.add(key, params.get(key));
			}
		}
		try {
			String responseString = mockMvc.perform(MockMvcRequestBuilders.post(uri).params(_params))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andDo(MockMvcResultHandlers.print())
					.andReturn().getResponse().getContentAsString();
			return responseString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}