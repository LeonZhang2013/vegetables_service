package com.zhongshu.vegetables.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class NetUtils {

    public static String httpGet(String url, Map<String, String> datas) {
        StringBuffer bufUrl = new StringBuffer(url);
        if (datas != null && datas.size() > 0) {
            bufUrl.append("?");
            for (Map.Entry item : datas.entrySet()) {
                bufUrl.append(item.getKey()).append("=").append(item.getValue()).append("&");
            }
            bufUrl.substring(bufUrl.length() - 1);
        }
        HttpGet httpGet = new HttpGet(bufUrl.toString());
        try {
            return getRequestData(httpGet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getRequestData(HttpRequestBase request) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse resp = httpClient.execute(request);
        try {
            HttpEntity entity = resp.getEntity();
            String str = EntityUtils.toString(entity, "UTF-8");
            return str;
        } finally {
            resp.close();
        }
    }

    public static String postStr(String strjs, String url) {
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity en = new StringEntity(strjs, "utf-8");
            httpPost.setEntity(en);
            return getRequestData(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(List<NameValuePair> params, String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(uefEntity);
            return getRequestData(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }

    public static boolean writeFile(FileInputStream fis,String outFilePath) {
        outFilePath = ".\\test\\image.png";

        boolean isOk = true;
        FileOutputStream fos = null;
        try {
            File file = new File(outFilePath);
            if(!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len=0;
            while((len=fis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                fos.flush();
            }
        } catch (IOException e) {
            isOk = false;
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    isOk = false;
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    isOk = false;
                    e.printStackTrace();
                }
            }
        }
        return isOk;
    }
}
