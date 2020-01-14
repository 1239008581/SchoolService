package com.worm.student.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class HtmlUtil {

    public static String getHtml(HttpClient httpClient, String url, String encode) {
        String html = null;
        //1.生成httpclient，相当于该打开一个浏览器
        HttpResponse response;
        //2.创建get请求，相当于在浏览器地址栏输入 网址
        HttpGet request = new HttpGet(url);
        try {
            //3.执行get请求，相当于在输入地址栏后敲回车键
            response = httpClient.execute(request);
            //4.判断响应状态为200，进行处理
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //5.获取响应内容
                HttpEntity httpEntity = response.getEntity();
                html = EntityUtils.toString(httpEntity, encode);
            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("返回状态不是200");
                System.out.println(EntityUtils.toString(response.getEntity(), encode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    /**
     * 获取并解析验证码
     * @param httpClient 浏览器
     * @param url 验证码地址
     * @param size 验证码长度
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    public static String analysisImage(HttpClient httpClient, String url, int size) throws IOException, TesseractException {
        String result;
        BufferedImage bufferedImage;

        ITesseract instance = new Tesseract();
        instance.setDatapath("D:\\tessdata\\");//字库存储文件存储文件
        instance.setLanguage("eng");// 选择字库文件（只需要文件名，不需要后缀名） mri是最小的能够匹配数字和字母的字库

        HttpGet httpGet = new HttpGet(url); // 创建Httpget实例

        do {
            HttpResponse response = httpClient.execute(httpGet);     // 执行http get请求
            HttpEntity entity = response.getEntity(); // 获取返回实体
            if(null == entity){
                return null;
            }
            InputStream inputStream = entity.getContent();//返回一个输入流
            bufferedImage = ImageIO.read(inputStream);
            bufferedImage = ImageHelper.getScaledInstance(bufferedImage, bufferedImage.getWidth()*4, bufferedImage.getHeight()*4);
            bufferedImage = ImageUtil.removeBackground(bufferedImage);
            bufferedImage = ImageUtil.setBufferImageType(bufferedImage, BufferedImage.TYPE_3BYTE_BGR);

            result = instance.doOCR(bufferedImage);
            result = StringUtils.replace(result, " ", "");
            result = result.substring(0,result.length()-1);
            inputStream.close();
        }while(result.length() != size);

        bufferedImage.flush();

        return result;
    }

//    public static void main(String[] args) {
//        try {
//            HttpClient client = HttpClientBuilder.create().build();
//            String result = HtmlUtil.analysisImage(client,"http://rz.ccsu.cn/authserver/captcha.html",4);
//            System.out.println("验证码:"+result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TesseractException e) {
//            e.printStackTrace();
//        }
//    }

}
