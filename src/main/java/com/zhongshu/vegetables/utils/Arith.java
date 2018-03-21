package com.zhongshu.vegetables.utils;

import com.zhongshu.vegetables.dao.beans.Freight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**  

 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精  
 
 * 确的浮点数运算，包括加减乘除和四舍五入。  
 
 */   
  
public final class Arith{   
  
    //默认除法运算精度   
    private static final int DEF_DIV_SCALE = 4;
    //单价精确位
    private static final int SALE_SCALE = 3;
    //单件精确位
    private static final int PIECE_SCALE = 2;

    //这个类不能实例化   
    private Arith(){   
  
    }   
  
    /**  
     * 提供精确的加法运算。  
     * @param v1 被加数  
     * @param v2 加数  
     * @return 两个参数的和  
     */   
  
    public static final double add(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.add(b2).doubleValue();   
    }


    /**  
     * 提供精确的减法运算。  
     * @param v1 被减数  
     * @param v2 减数  
     * @return 两个参数的差  
     */   
  
    public static final double sub(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.subtract(b2).doubleValue();   
    }   
      
    /**  
     * 提供精确的乘法运算。  
     * @param v1 被乘数  
     * @param v2 乘数  
     * @return 两个参数的积  
     */   
  
    public static final double mul(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.multiply(b2).doubleValue();   
    }

    public static final double mul(String v1,String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**  
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到  
     * 小数点以后4位，以后的数字四舍五入。  
     * @param v1 被除数  
     * @param v2 除数  
     * @return 两个参数的商  
     */   
  
    public static final double div(double v1,double v2){   
        return div(v1,v2,DEF_DIV_SCALE);   
    }   
    /**  
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指  
     * 定精度，以后的数字四舍五入。  
     * @param v1 被除数  
     * @param v2 除数  
     * @param scale 表示表示需要精确到小数点以后几位。  
     * @return 两个参数的商  
     */   
  
    public static final double div(double v1,double v2,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        if(v2 == 0){  
            return 0;  
        }  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
    }   
  
    /**  
     * 提供精确的小数位四舍五入处理。  
     * @param v 需要四舍五入的数字  
     * @param scale 小数点后保留几位  
     * @return 四舍五入后的结果  
     */   
  
    public static final double round(double v,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b = new BigDecimal(Double.toString(v));   
        BigDecimal one = new BigDecimal("1");   
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
    }   
      
    /** 
     * 将科学计数法转换为double类型 
     * @param num 
     * @return 
     */  
    public static final BigDecimal convertDouble(double num){  
        BigDecimal big = new BigDecimal(num);  
        return big.setScale(10, 2);  
    }  
      
    /** 
     * 判断两个浮点数是否相等 
     * 设定一个精度，如果两个数相减的绝对值相等，说明相等 
     * @param d1 
     * @param d2 
     * @return 
     */  
    public static final boolean equals(double d1,double d2){  
        return Math.abs(sub(d1, d2)) <= 0.0000001;  
    }

    public static void calProductSalePrice(List<Map<String, Object>> maps, Float user_percent){
        calShopCartPrice(maps,new BigDecimal(user_percent),null);
    }
    public static void calProductSalePrice(List<Map<String, Object>> maps, BigDecimal user_percent){
        calShopCartPrice(maps,user_percent,null);
    }

    /**
     * 计算商品价格   仅仅用于  提交订单之前。
     * @param maps 商品 （price，percent_price，num，other_charge）
     * @param userPerFreight  对应map num 的Key.    PS(map.put("buy_num",8)) 这里传 key 就是 buy_num
     * @param user_percent （代理商 利润点+）
     * @return 返回商品总价（包含杂费和利润点，不包含运费）。
     */
    public static BigDecimal calShopCartPrice(List<Map<String, Object>> maps, BigDecimal user_percent, BigDecimal userPerFreight) {
        BigDecimal productTotalPrice = new BigDecimal(0);
        for (int i=0; i<maps.size(); i++){
            Map<String, Object> map = maps.get(i);
            BigDecimal price = new BigDecimal(map.get("price").toString());
            BigDecimal percent = new BigDecimal(map.get("percent_price").toString());
            BigDecimal num = new BigDecimal(map.get("num").toString());
            BigDecimal other_charge = new BigDecimal(map.get("other_charge").toString());
            BigDecimal devPercent = percent.divide(new BigDecimal(100)).add(new BigDecimal(1));
            BigDecimal devUserPercent = user_percent.divide(new BigDecimal(100)).add(new BigDecimal(1));
            //公司一件的售价（件） = （进货价 * 公司利润点/100  + 进货价） * 单件数量 + 单件的其他杂费。
            BigDecimal piece_price = price.multiply(num).multiply(devPercent).add(other_charge);
            //代理商一件的售价（件） = 公司售价+ 公司售价＊商家利润点／１００；
            piece_price = piece_price.multiply(devUserPercent);
            //=======================================================================================
            BigDecimal priceSale = piece_price.divide(num,BigDecimal.ROUND_UP).setScale(SALE_SCALE,BigDecimal.ROUND_UP);
            BigDecimal pieceSale = priceSale.multiply(num).setScale(PIECE_SCALE,BigDecimal.ROUND_UP);
            //一斤的售价(单价) = 销售价/ 单件数量
            map.put("price_sale",priceSale);
            map.put("piece_price",pieceSale);
            if(userPerFreight!=null){
                BigDecimal buyNum = new BigDecimal(map.get("buy_num").toString());
                BigDecimal subProductPrice = pieceSale.multiply(buyNum);
                BigDecimal weight = new BigDecimal(map.get("weight").toString());
                BigDecimal subWeight = weight.multiply(buyNum);
                BigDecimal subFreight = subWeight.multiply(userPerFreight).divide(new BigDecimal(2000));
                map.put("sub_freight",subFreight);
                map.put("sub_price",subProductPrice);
                // 累加
                productTotalPrice = productTotalPrice.add(subProductPrice).add(subFreight);
            }
        }
        return productTotalPrice;
    }



    /**
     * 只计算运费
     * @param maps  （必须要有  buy_num 和 weight）
     * @param charge
     * @return
     */
    public static Freight calTotalFreight(List<Map<String, Object>> maps, BigDecimal charge) {
        Freight freight = new Freight();
        BigDecimal totalWeight = new BigDecimal(0);
        BigDecimal totalFreight = new BigDecimal(0);

        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            BigDecimal num = new BigDecimal(map.get("buy_num").toString());
            BigDecimal weight = new BigDecimal(map.get("weight").toString());
            BigDecimal subFreight = num.multiply(weight).multiply(charge).divide(new BigDecimal(2000),DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP);
            totalFreight = totalFreight.add(subFreight);
            totalWeight = totalWeight.add(num.multiply(weight));
        }
        //一吨 = 2000 斤  totalWeight 是总斤数， charge代表 到达目的地 每吨多少钱。
        freight.setFreight(totalFreight.setScale(PIECE_SCALE,BigDecimal.ROUND_HALF_UP));
        freight.setWeight(totalWeight);
        return freight;
    }
}