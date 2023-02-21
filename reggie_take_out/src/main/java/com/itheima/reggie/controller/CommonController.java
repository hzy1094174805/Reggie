package com.itheima.reggie.controller;

import com.itheima.reggie.common.QiNiuClient;
import com.itheima.reggie.common.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/**
 * 常见控制器
 *
 * @author ilovend
 * @date 2023/02/17
 */
@RestController
@RequestMapping("/common")
@Slf4j
@Api(tags = "图片上传下载")
public class CommonController {

@Autowired
private QiNiuClient qiNiuClient;

    /**
     * 上传
     *
     * @param file 文件
     * @return {@link R}<{@link String}>
     * @throws IOException
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();//1.jpg
        int index = originalFilename.lastIndexOf(".");//  1
        String suffix = originalFilename.substring(index);//.jpg
        //随机生成图片的名字  gutrwaestygiutrawstgiuhiset
        String fileName = UUID.randomUUID().toString().replace("-","")+suffix;
        //上传到七牛云
        String url = qiNiuClient.transferQiNiuOss(fileName, file.getInputStream());
        return R.success(url);
    }

/*
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //获取文件的输入流
        FileInputStream fileInputStream = new FileInputStream(baseUrl + name);
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");

        byte[] bytes = new byte[1024];
        int len;
        while ((len =fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        fileInputStream.close();
    }*/
}
