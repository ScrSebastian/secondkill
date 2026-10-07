package com.xxxx.secondkill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        // 获取项目根目录
        String projectPath = System.getProperty("user.dir");

        // 1. 使用 FastAutoGenerator 创建生成器实例
        FastAutoGenerator.create(
                        "jdbc:mysql://localhost:3306/secondkill?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=GMT%2B8",
                        "root",
                        "1a1a1a1a"
                )
                // 2. 全局配置
                .globalConfig(builder -> {
                    builder.author("seb")          // 设置作者
                            .disableOpenDir()            // 禁止打开输出目录
                            .outputDir(projectPath + "/src/main/java"); // 指定输出目录
                })
                // 3. 包配置与自定义 XML 路径
                .packageConfig(builder -> {
                    builder.parent("com.xxxx.secondkill") // 父包名
                            .entity("pojo")
                            // 对应旧版的 InjectionConfig：自定义 XML 的输出路径
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    projectPath + "/src/main/resources/mapper"
                            ));
                })
                // 4. 模板配置（核心：完全对应 3.5.X 的自定义模板设置）
                .templateConfig(builder -> {
                    // 这里的路径指向 classpath:/templates/ 下的自定义模板
                    // 注意：传入模板路径时不需要写 .ftl 后缀，引擎会自动拼接
                    builder.entity("/templates/entity2.java")
                            .mapper("/templates/mapper2.java")
                            .service("/templates/service2.java")
                            .serviceImpl("/templates/serviceImpl2.java")
                            .controller("/templates/controller2.java");
                })
                // 5. 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("goods", "t_order", "t_seckill_goods", "t_seckill_order")
                            .addTablePrefix("t_")

                            // 实体类策略
                            .entityBuilder()
                            .idType(IdType.ASSIGN_ID)
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .enableFileOverride()

                            // Mapper 策略配置（核心修改点就在这里！）
                            .mapperBuilder()
                            .enableMapperAnnotation()
                            .enableBaseResultMap()   // 开启生成 BaseResultMap（对应教程里的 resultMap）
                            .enableBaseColumnList()  // 开启生成 BaseColumnList（对应教程里的 sql 标签）
                            .enableFileOverride()    // 记得允许覆盖旧文件，否则重新生成不生效

                            // Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride()

                            // Controller 策略配置
                            .controllerBuilder()
                            .enableRestStyle()
                            .enableFileOverride();
                })
                // 6. 显式指定模板引擎（必须与你的 pom.xml 依赖一致）
                .templateEngine(new FreemarkerTemplateEngine())
                // 7. 执行生成
                .execute();
    }
}
