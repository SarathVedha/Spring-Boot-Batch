package com.vedha;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Batch APP", version = "1.0", description = "Batch APP"))
public class SpringBootBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBatchApplication.class, args);
    }

}
