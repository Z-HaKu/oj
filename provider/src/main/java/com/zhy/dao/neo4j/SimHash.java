package com.zhy.dao.neo4j;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

public class SimHash {

    int[] v;

    private BigInteger intSimHash;

    public String strSimHash;

    private int hashbits = 64;


    // 通过自定义的查询结果集 resultjson 获取其相应的Simhash对象
    public static SimHash getSimhash(ResultJSON rj) throws IOException {
        SimHash simHash = new SimHash(64);
        for (JSONObject jo :
                rj.result) {
//            System.out.println(jo.toJSONString());
            simHash.addToken(jo.toJSONString());
        }
        simHash.setSimHash();
        return simHash;
    }

    public static SimHash getSimhashWithoutSet(ResultJSON rj) throws IOException {
        SimHash simHash = new SimHash(64);
        for (JSONObject jo :
                rj.result) {
            //            System.out.println(jo.toJSONString());
            simHash.addToken(jo.toJSONString());
        }
        return simHash;
    }


    public SimHash(int hashbits) throws IOException {
        this.hashbits = hashbits;
        v = new int[this.hashbits];
    }

    HashMap<String, Integer> wordMap = new HashMap<String, Integer>();


    // 向simhash对象中添加Token来获取 更新其特征向量和数组 v[]
    public void addToken(String word){
        BigInteger t = this.hash(word);
        for (int i = 0; i < this.hashbits; i++) {
            BigInteger bitmask = new BigInteger("1").shiftLeft(i);
            // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
            // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
            // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
            if (t.and(bitmask).signum() != 0) {
                // 这里是计算整个文档的所有特征的向量和

                v[i] += 1;
            } else {
                v[i] -= 1;
            }
        }
    }


    // 根据已生成的特征向量和数据生成 strsimhash以及 返回simhash文件签名值
    public BigInteger setSimHash() {
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
//        System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        intSimHash = fingerprint;
//        System.out.println(intSimHash);
        return fingerprint;
    }

//    public BigInteger simHash() throws IOException {
//        // 定义特征向量/数组
//        int[] v = new int[this.hashbits];
//
//        String word = null;
//        String temp = null;
//        while ((lexeme = ik.next()) != null) {
//            word = lexeme.getLexemeText();
//            // 注意停用词会被干掉
//            // System.out.println(word);
//            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
//            BigInteger t = this.hash(word);
//            for (int i = 0; i < this.hashbits; i++) {
//                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
//                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
//                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
//                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
//                if (t.and(bitmask).signum() != 0) {
//                    // 这里是计算整个文档的所有特征的向量和
//                    // 这里实际使用中需要 +- 权重，比如词频，而不是简单的 +1/-1，
//                    v[i] += 1;
//                } else {
//                    v[i] -= 1;
//                }
//            }
//        }
//
//        BigInteger fingerprint = new BigInteger("0");
//        StringBuffer simHashBuffer = new StringBuffer();
//        for (int i = 0; i < this.hashbits; i++) {
//            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
//            if (v[i] >= 0) {
//                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
//                simHashBuffer.append("1");
//            } else {
//                simHashBuffer.append("0");
//            }
//        }
//        this.strSimHash = simHashBuffer.toString();
//        System.out.println(this.strSimHash + " length " + this.strSimHash.length());
//        return fingerprint;
//    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
        // 对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

//    public int getDistance(String str1, String str2) {
//        int distance;
//        if (str1.length() != str2.length()) {
//            distance = -1;
//        } else {
//            distance = 0;
//            for (int i = 0; i < str1.length(); i++) {
//                if (str1.charAt(i) != str2.charAt(i)) {
//                    distance++;
//                }
//            }
//        }
//        return distance;
//    }

//    public List subByDistance(utils.SimHash simHash, int distance) {
//        // 分成几组来检查
//        int numEach = this.hashbits / (distance + 1);
//        List characters = new ArrayList();
//
//        StringBuffer buffer = new StringBuffer();
//
//        int k = 0;
//        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
//            // 当且仅当设置了指定的位时，返回 true
//            boolean sr = simHash.intSimHash.testBit(i);
//
//            if (sr) {
//                buffer.append("1");
//            } else {
//                buffer.append("0");
//            }
//
//            if ((i + 1) % numEach == 0) {
//                // 将二进制转为BigInteger
//                BigInteger eachValue = new BigInteger(buffer.toString(), 2);
//                System.out.println("----" + eachValue);
//                buffer.delete(0, buffer.length());
//                characters.add(eachValue);
//            }
//        }
//
//        return characters;
//    }
}
