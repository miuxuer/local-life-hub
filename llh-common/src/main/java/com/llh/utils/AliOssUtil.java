package com.llh.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.llh.properties.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传工具类（OSS Java SDK V2）
 */
@Component
@Slf4j
public class AliOssUtil {

    @Autowired
    private AliOssProperties aliOssProperties;

    /**
     * 文件上传：按 年/月 分目录，文件名用 UUID 防重名
     *
     * @param content          文件字节内容
     * @param originalFilename 原始文件名（用于取扩展名）
     * @return 文件的可访问 URL
     */
    public String upload(byte[] content, String originalFilename) throws Exception {
        String endpoint = aliOssProperties.getEndpoint();
        String bucketName = aliOssProperties.getBucketName();
        String region = aliOssProperties.getRegion();

        // 从环境变量读 AK/SK。运行前确保已设置 OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();

        // 生成按"年/月"分目录、且不重名的文件路径，例如 2026/09/uuid.png
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = dir + "/" + newFileName;

        // 创建 OSS V2 客户端（链式 builder）
        OSSClientBuilder builder = OSSClient.newBuilder()
                .endpoint(endpoint)
                .region(region)
                .credentialsProvider(credentialsProvider);

        // OSSClient 实现了 AutoCloseable，try-with-resources 自动关闭
        try (OSSClient client = builder.build()) {
            PutObjectRequest request = PutObjectRequest.newBuilder()
                    .bucket(bucketName)
                    .key(objectName)
                    .body(BinaryData.fromBytes(content))
                    .build();
            client.putObject(request);
        }

        // 拼出可访问的 URL：https://<bucket>.<endpoint域名>/<objectName>
        String url = endpoint.split("//")[0] + "//" + bucketName + "."
                + endpoint.split("//")[1] + "/" + objectName;
        log.info("文件上传到:{}", url);
        return url;
    }
}
