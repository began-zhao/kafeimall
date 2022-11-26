package com.kafeimall.product.service.impl;

import com.kafeimall.common.event.DomainEventPublisher;
import com.kafeimall.common.result.Result;
import com.kafeimall.product.domain.aggregate.CategoryAggregate;
import com.kafeimall.product.domain.aggregate.SkuAggregate;
import com.kafeimall.product.domain.aggregate.SpuAggregate;
import com.kafeimall.product.domain.entity.SeckillInfo;
import com.kafeimall.product.domain.eventHandles.model.ProductEventEto;
import com.kafeimall.product.infrastructure.facade.SeckillAdaptor;
import com.kafeimall.product.infrastructure.repo.repository.CategoryRepository;
import com.kafeimall.product.infrastructure.repo.repository.SkuInfoRepository;
import com.kafeimall.product.infrastructure.repo.repository.SpuInfoRepository;
import com.kafeimall.product.service.ProductDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: zzg
 * @date: 8/23/22
 * @Description: 订单领域服务实现
 */
@Service
public class ProductDomainServiceImpl implements ProductDomainService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CategoryRepository categoryRepository;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SpuInfoRepository spuInfoRepository;
    @Autowired
    private SkuInfoRepository skuInfoRepository;
    @Autowired
    private SeckillAdaptor seckillAdaptor;
    @Autowired
    ThreadPoolExecutor executor;

    @Resource
    DomainEventPublisher domainEventPublisher;


    @Override
    public List<CategoryAggregate> getCategory() {
        List<CategoryAggregate> productCategory = categoryRepository.getProductCategory();
        return productCategory;
    }

    @Override
    public void updateCategoryById(CategoryAggregate categoryAggregate) {
        categoryRepository.updateCategoryById(categoryAggregate);

        //测试：品类修改后发送通知
        domainEventPublisher.publishEvent(new ProductEventEto());
    }
    @EventListener
    public void productLog(ProductEventEto productEventEto){
        System.out.println(productEventEto.toString());
    }

    @EventListener
    public void productStatus(ProductEventEto productEventEto){
        System.out.println(productEventEto.toString());
    }

    @Override
    public SpuAggregate getSpuInfo(Long spuId) throws ExecutionException, InterruptedException {
        SpuAggregate spuAggregate = spuInfoRepository.getBySpuId(spuId);
        return spuAggregate;
    }

    @Override
    public SkuAggregate getSkuInfo(Long skuId) throws ExecutionException, InterruptedException {
        //查询当前sku是否参与秒杀优惠
        CompletableFuture<SeckillInfo> secKillFuture = CompletableFuture.supplyAsync(() -> {
            Result<SeckillInfo> skuSeckillInfo = seckillAdaptor.getSkuSeckillInfo(skuId);
            return skuSeckillInfo.getData();
        }, executor);
        SkuAggregate skuAggregate = skuInfoRepository.getById(skuId);
        skuAggregate.setSeckillInfo(secKillFuture.get());
        return skuAggregate;
    }

}