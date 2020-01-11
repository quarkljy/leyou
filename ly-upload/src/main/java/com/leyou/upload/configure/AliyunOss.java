package com.leyou.upload.configure;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.StorageClass;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "ly.upload")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class AliyunOss {
    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private List<String> allowTypes;

    private String baseUrl ;


    public String upload(MultipartFile file) {
        String objectName = null;
        OSS ossClient = null;
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String replace = UUID.randomUUID().toString().replace("-", "");
            objectName = replace +"-"+ originalFilename;
            ossClient=new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, objectName, inputStream);
            return baseUrl + objectName;

        } catch (Exception e) {
            log.error("[文件上传] AliyunOss出错： {}", e);
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return null;
    }

    @PostConstruct
    private void init() {
        this.baseUrl = endpoint.substring(0, 7) + bucketName + "." + endpoint.substring(7) + "/";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        boolean exists = ossClient.doesBucketExist(bucketName);
        if (!exists) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            createBucketRequest.setStorageClass(StorageClass.Standard);
            // 创建存储空间。
            ossClient.createBucket(createBucketRequest);
            //公共读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }
    }

}
