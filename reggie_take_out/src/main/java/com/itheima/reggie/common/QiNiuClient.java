package com.itheima.reggie.common;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class QiNiuClient {

    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value("${qiniu.secretKey}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("${qiniu.domain}")
    private String domain;

   public String transferQiNiuOss(String fileName, InputStream file) {

       //构造一个带指定Region对象的配置类
       Configuration cfg = new Configuration(Region.regionAs0());
       cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释
       UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传

       fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/")) + fileName;
       Auth auth = Auth.create(accessKey, secretKey);
       String upToken = auth.uploadToken(bucket);
       try {
           Response response = uploadManager.put(file, fileName, upToken, null, null);
//           Response response = uploadManager.put(localFilePath, key, upToken);
           //解析上传成功的结果
           DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
           System.out.println(putRet.key);
           System.out.println(putRet.hash);
       } catch (QiniuException ex) {
           Response r = ex.response;
           System.err.println(r.toString());
           try {
               System.err.println(r.bodyString());
           } catch (QiniuException ex2) {
               //ignore
           }
       }
       return domain + fileName;
   }
}
