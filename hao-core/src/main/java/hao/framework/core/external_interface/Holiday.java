package hao.framework.core.external_interface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import hao.framework.core.utils.JsonUtil;



/**
 * 节假日接口，传入开始时间结束时间，返回工作日，周末，节假日的情况
 * @author chianghao
 *
 */
public class Holiday {
    /**
     * @param urlAll
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request( String httpArg) {
        String httpUrl="http://tool.bitefu.net/jiari";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?d=" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            Map<String,Object> map=  JsonUtil.json2MapMoryLayer(result);//.toMap(result);
            String res=(String)map.get(httpArg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void main(String[] args) {
        //判断今天是否是工作日 周末 还是节假日
        SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");
        String httpArg=f.format(new Date());
        System.out.println(httpArg);
        String jsonResult = request(httpArg);
        Map<String,Object> map=JsonUtil.json2MapMoryLayer(jsonResult);////JsonUtil.toMap(jsonResult);
        String res=(String)map.get(f.format(new Date()));
        System.out.println(res);

         //0 上班 1周末 2节假日
    }

}