package com.vedha.service;

import com.vedha.entity.OrganizationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service // to make the bean singleton
@StepScope // to make the bean step scope, so bean will be created for each step
public class CustomItemProcessor implements ItemProcessor<OrganizationEntity, OrganizationEntity> {

    private final AtomicInteger count = new AtomicInteger(0);

    private final Long minEmployees;

    public CustomItemProcessor(@Value("#{jobParameters['minEmployees']}") Long minEmployees) {
        this.minEmployees = minEmployees;
    }

    @Override
    public OrganizationEntity process(OrganizationEntity item) throws Exception {

        if (item.getEmployees() > minEmployees) {
            return item;
        } else {
            log.warn("item {}, skipped: {}", count.incrementAndGet(), item);
            return null;
        }
    }
}
