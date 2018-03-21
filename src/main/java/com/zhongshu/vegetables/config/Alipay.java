package com.zhongshu.vegetables.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

public class Alipay {

    public static final String APP_ID = "2018011501876519";
    public static final String GATEWAY = "https://openapi.alipay.com/gateway.do";
    public static final String CHARSET = "utf-8";
    public static final String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCBz6VvkJ/aT2zp4l1rf+AQCmtLhDcOR36NbduO77ce6oS2k/zQZast+JR4X5wSQNkg6ayVYmX6iH4+83IzN5bSk52BFqb8gGNeskn6mn5qVXBzf+ukzXF9IiFZ3Ii3pXLd0SzLq5Qc3DnaLQIU2IaHHPvV5MSubiq3VErvbOCJMx1BEeJ+aa1fsYcqOPsR7ecfXM6HVvKKQa8uIjlKnpKkfRKABzGQgmRHx5yACSvqD5EwB6CSQvrNMrfen9Vg6EonjDAkX7/D7XCVFjwiIokv4EJogFKGnZdSEe5ADaOttaKTRMnO2IM2f3EMmfmEPBgoMh8jq1ZR+pG4awzfUdw1AgMBAAECggEAJJ6nVOm/rp5pYzFwmTrhwqSt0AfkYCrbEJrFLXWCez49YiQtVbzYpYdSmwKdzKhumFJTVXEEw8BtnOSgesJb0dvCuv/g66psXfJ93CxpLSYdKvgTSdKLCXnvXqJQDz6lQN4ZfPtSNTILP/kidS8ABG2wvY5jzbFWa5Fj8lcMoKdb1kRfhI0fkEd5hvv8YE2GqWQhhRFFUNkbtKs/BNKaIflC707Hp5aX9sa8NE/Fze/yfMigp4fTjWAq1/5Ge6nNS0LjmmbvmwFSm1vpSdmRJWDpRAi9VjKp1UG3mtw42uA5wzjdjn8+FXJFj+hxriCS+Ah/+Q+U28ziwsDP3dr8oQKBgQC86zJsIDkY7dgsLrj4ERoVxe8VdZtzWoaJJvewsGq6oBQAl7YNBXGb1FnjI8+tANNxoirJJqPdkWCd0E29HYZXRUs6Fnkyg/ozuHTItZ0qQrEhGg6WXzK1QsmXPh7QiXqQPKvu44wlJpoftD4CKxcrZqM59OFlifgsq2bQFKOEAwKBgQCv54ndQ8LbJ6S2ZIx7k8yFX3uHiVSN499PC5yUkKdJDgA1karibvP84jifLN5o5g2z7TJIvZaKt7nW0EV80dWx5lmRnol5LDgsLkrCxP2FDCwocDPnXupiVnYiGs3fj45rr4M+SvDqPN2Fmb5sPDNrzlwpEOLIaZrzgw2BmeaVZwKBgHXyESTYaU1bEN9kvC6D3tlBHiczqb23As+V+IXjXn7teg90qEAw6eD0Drp0nS/RmDEVoci7ywqFvOBKMH6ldD4AERtO9JPJOegYfTDh0iGUSan07q6K+MmZzOoT6oEfk9mBR4Z5ogF/vDGXAi+wX2LFTZrde+s83ChnSmUWvY+pAoGAMmKL5skGA2gzkdrzMmJ/bqSO6Z+4jHv89tNIonVctVxBSxbSb9GAKatStKVRf5KF0kfa42MKv+koXhOCE+K25yIn/cH/dqnn3R/VYjyysW1vKJYbB+b0E/7YD6TF24dvMAEyAqHMqAuPGDmVhcMop3SQiWLA0s8NBzs+nyTKG3kCgYA5AqGI6YosSgxGJ72b+4i7uR6b8/VVQoC26EYSI8RR+b5a/g5gVMFWeaYUysPyusG2B9qw7L6nTJX6dt5dGeLq4blqYuAn+ABJVXpiJ3hCasbQMeDD83vZTRiwHUEF3/GmRg4AWAFqh99/9sGOKpUOLZDgCOY3Fp34qTSRAiIemQ==";
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhkaYAheEoRvFMOpnUpDLXovtvF3rhptnD4IHJpi2pVAHocpF+92Gi/H7QJtZ4eAecyko3XjiGhcK+a+mesJ/QL0fudCOD5KFCvBa0bjejFwAjfkwOrPhEkFuGFXPnPno1eihV32NuIfBX8RWYZOa3uX97e+HFRCapM8xLy9iNK2fYRWOOW03u4A7AM7GSanbtm0FLq2TFyhZwZnyqvlKq24yO4qV5u/odP/BNe1A+CutA/RPDB/m/6J/V6tI2SeczHsSyuDvzeNPufBKQ7F6dxnBn4N25I6LAe30kJrt0p61cVh2vojAOvkbFOHVLbJ8+aWAfDb4yic1EU4S/G//QQIDAQAB";
    public static final String APP_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgc+lb5Cf2k9s6eJda3/gEAprS4Q3Dkd+jW3bju+3HuqEtpP80GWrLfiUeF+cEkDZIOmslWJl+oh+PvNyMzeW0pOdgRam/IBjXrJJ+pp+alVwc3/rpM1xfSIhWdyIt6Vy3dEsy6uUHNw52i0CFNiGhxz71eTErm4qt1RK72zgiTMdQRHifmmtX7GHKjj7Ee3nH1zOh1byikGvLiI5Sp6SpH0SgAcxkIJkR8ecgAkr6g+RMAegkkL6zTK33p/VYOhKJ4wwJF+/w+1wlRY8IiKJL+BCaIBShp2XUhHuQA2jrbWik0TJztiDNn9xDJn5hDwYKDIfI6tWUfqRuGsM31HcNQIDAQAB";
    public static final String FORMAT = "json";
    public static final String SIGN_TYPE = "RSA2";
    public static final String LISTENER = "http://47.98.18.193/vegetable/pay/listener";

    public static AlipayClient generateAlipayClient() {
        return new DefaultAlipayClient(
                Alipay.GATEWAY,
                Alipay.APP_ID,
                Alipay.APP_PRIVATE_KEY,
                Alipay.FORMAT,
                Alipay.CHARSET,
                Alipay.ALIPAY_PUBLIC_KEY,
                Alipay.SIGN_TYPE);
    }


    public static AlipayClient generateAlipayClient(String charset) {
        return new DefaultAlipayClient(
                Alipay.GATEWAY,
                Alipay.APP_ID,
                Alipay.APP_PRIVATE_KEY,
                Alipay.FORMAT,
                charset,
                Alipay.ALIPAY_PUBLIC_KEY,
                Alipay.SIGN_TYPE);
    }

}


