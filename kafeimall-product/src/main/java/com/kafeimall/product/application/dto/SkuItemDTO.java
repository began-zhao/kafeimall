package com.kafeimall.product.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: zzg
 * @date: 9/1/22
 * @Description: SKU详情
 */
@Data
@NoArgsConstructor
public class SkuItemDTO {
    //1、sku基本信息获取 pms_sku_info
    SkuInfoDTO skuInfo;

    //是否有货
    boolean hasStock = true;

    //2、sku的图片信息获取 pms_sku_images
    List<SkuImageDTO> images;

    //3、spu销售属性组合
    List<SkuItemSaleAttrDTO> saleAttr;

    //4、获取spu介绍
    SpuInfoDescDTO desp;

    //5、获取spu规格参数信息
    List<SpuItemAttrGroupDTO> groupAttrs;

    //6、当亲商品秒杀优惠信息
    SeckillInfoDTO seckillInfo;
}
