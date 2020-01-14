package com.worm.student.service.impl;

import com.worm.student.pojo.Grade;
import com.worm.student.service.StudentService;
import com.worm.student.utils.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void searchTimetable(String studentId, String openingHours, Integer week) {

        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet("http://jwcxxcx.ccsu.cn/jwxt/Logon.do?method=logonSSO&uid=" + studentId + "&ads=3");
            client.execute(get);

            get = new HttpGet("http://jwcxxcx.ccsu.cn/jwxt/tkglAction.do?method=goListKbByXs&istsxx=no&xnxqh=" + openingHours + "&zc=" + week + "&xs0101id=" + studentId);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String entityMsg = EntityUtils.toString(entity);

            HtmlCleaner htmlCleaner = new HtmlCleaner();
            TagNode treeNode;
            Object[] nodes;
            treeNode = htmlCleaner.clean(entityMsg);
            nodes = treeNode.evaluateXPath("//*[@id=\"kbtable\"]/tbody/tr");
            for (int k = 1; k < nodes.length - 1; k++) {
                TagNode node = (TagNode) nodes[k];
                List<TagNode> nodeChild = node.getChildTagList();
                for (int i = 1; i < nodeChild.size(); i++) {
                    List<TagNode> nodeGrandson = nodeChild.get(i).getChildTagList();
                    System.out.print(nodeGrandson.get(1).getText().toString() + " ");
                }
                System.out.println();
            }
        } catch (XPatherException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Grade> searchGrade(String studentId, String openingHours) throws Exception {

        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet("http://jwcxxcx.ccsu.cn/jwxt/Logon.do?method=logonSSO&uid=" + studentId + "&ads=3");
            client.execute(get);

            get = new HttpGet("http://jwcxxcx.ccsu.cn/jwxt/xszqcjglAction.do?method=queryxscj&kksj=" + openingHours);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String entityMsg = EntityUtils.toString(entity);

            HtmlCleaner htmlCleaner = new HtmlCleaner();
            TagNode treeNode;
            Object[] nodes;
            treeNode = htmlCleaner.clean(entityMsg);
            List<Grade> gradeList = new ArrayList<Grade>();
            nodes = treeNode.evaluateXPath("//*[@id=\"mxh\"]/tbody/tr");
            for (Object object : nodes) {
                TagNode node = (TagNode) object;
                List<TagNode> nodeChild = node.getChildTagList();
                gradeList.add(
                        Grade.builder()
                                .studentId(nodeChild.get(2).getText().toString())
                                .studentName(nodeChild.get(3).getText().toString())
                                .openingHours(nodeChild.get(4).getText().toString())
                                .courseName(nodeChild.get(5).getText().toString())
                                .courseGrade(nodeChild.get(6).getText().toString())
                                .courseNature(nodeChild.get(8).getText().toString())
                                .courseCategory(nodeChild.get(9).getText().toString())
                                .coursePeriod(nodeChild.get(10).getText().toString())
                                .courseCredits(nodeChild.get(11).getText().toString())
                                .examNature(nodeChild.get(12).getText().toString())
                                .build()
                );
            }
            return gradeList;
        } catch (XPatherException | IOException e) {
            log.error(e.getMessage());
            throw new Exception();
        }
    }

    public static HttpClient loginOne(String Username, String Password) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://jwcxxcx.ccsu.cn/jwxt/Logon.do?method=logon");
        //登录表单的信息
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("USERNAME", Username));
        qparams.add(new BasicNameValuePair("PASSWORD", Password));

        UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, Consts.UTF_8);
        post.setEntity(params);
        //相当于按了下确定登录的按钮，也就是浏览器调转了
        client.execute(post);
        return client;
    }

    public static HttpClient loginTwo(HttpClient client, String Username, String Password, int number) throws IOException, XPatherException, TesseractException {
        String html = HtmlUtil.getHtml(client, "http://rz.ccsu.cn/authserver/login", "utf-8");
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode treeNode = htmlCleaner.clean(html);
        Object[] nodes = treeNode.evaluateXPath("//*[@id=\"casLoginForm\"]/input");
        HttpPost post = new HttpPost("http://rz.ccsu.cn/authserver/login?service=http%3A%2F%2Fentry.ccsu.cn%2F");
        //尝试登陆五次
        for (int i = 0; i < number; i++) {
            String Captcha = HtmlUtil.analysisImage(client, "http://rz.ccsu.cn/authserver/captcha.html", 4); //获取验证码
            //登录表单的信息
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("username", Username));
            qparams.add(new BasicNameValuePair("password", Password));
            qparams.add(new BasicNameValuePair("captchaResponse", Captcha));
            qparams.add(new BasicNameValuePair("lt", ((TagNode) nodes[0]).getAttributeByName("value")));
            qparams.add(new BasicNameValuePair("dllt", ((TagNode) nodes[1]).getAttributeByName("value")));
            qparams.add(new BasicNameValuePair("execution", ((TagNode) nodes[2]).getAttributeByName("value")));
            qparams.add(new BasicNameValuePair("_eventId", ((TagNode) nodes[3]).getAttributeByName("value")));
            qparams.add(new BasicNameValuePair("rmShown", ((TagNode) nodes[4]).getAttributeByName("value")));
            UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, Consts.UTF_8);
            post.setEntity(params);
            //相当于按了下确定登录的按钮，也就是浏览器调转了
            HttpResponse response = client.execute(post);
            if ((response.getHeaders("Set-Cookie")).length > 0) {
                return client;
            }
        }
        System.out.println("抱歉！登录失败~~");
        return null;
    }

}
