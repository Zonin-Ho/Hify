package com.example.demo.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.util.StringUtils;

import java.net.URL;

@Slf4j
public class IPUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOOPBACK_ADDRESS = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST = "127.0.0.1";

    // 私有构造函数，禁止实例化
    private IPUtil() {
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };

        String ip = null;
        for (String header : headers) {
            ip = request.getHeader(header);
            if (StringUtils.hasLength(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
                break;
            }
        }

        if (!StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果IP地址是IPv6的本地地址，转换为IPv4的127.0.0.1
        if (LOOPBACK_ADDRESS.equals(ip)) {
            return LOCALHOST;
        }

        // 如果IP地址包含多个代理服务器地址，取第一个非unknown的IP地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        return ip;
    }

    /**
     * 根据IP地址获取物理地址信息
     *
     * @param ip IP地址
     * @return 物理地址信息
     */
    public static String getAddr(String ip) {
        if (LOCALHOST.equals(ip)) {
            return "本地";
        }
        try {
            // 使用当前类的类加载器
            ClassLoader classLoader = IPUtil.class.getClassLoader();
            // 获取资源 URL
            URL resourceUrl = classLoader.getResource("ip2region/ip2region.xdb");
            if (resourceUrl == null) {
                log.info("Resource not found");
                return null;
            }

            String dbPath = resourceUrl.getPath();
            log.info("dbPath: " + dbPath);

            // 1、读取资源文件内容
            byte[] cBuff = Searcher.loadContentFromFile(dbPath);

            // 2、使用缓存创建查询对象
            Searcher searcher = Searcher.newWithBuffer(cBuff);

            // 3、查询IP地址对应的位置信息
            String region = searcher.search(ip);
            return region;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("failed to search(%s): %s\n", ip, e);
            return null;
        }
    }

}

