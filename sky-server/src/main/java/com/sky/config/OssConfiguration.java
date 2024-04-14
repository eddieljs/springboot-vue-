package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.control.CodeGenerationHint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能：配置类，用于创建Alioss对象
 * 作者：ljs
 * 日期：2024/4/14 23:13
 */
@Configuration
@Slf4j
public class OssConfiguration{
        @Bean
        @ConditionalOnMissingBean
        public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
            log.info("开始创建阿里云oss对象，{}",aliOssProperties);
            return new AliOssUtil(aliOssProperties.getEndpoint(),aliOssProperties.getAccessKeyId(),
                    aliOssProperties.getAccessKeySecret(),aliOssProperties.getBucketName());
        }
}