package com.lhsdev.cmsproject.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class GeoIpService {

    // KISA or IPDeny KR Zone URL
    private static final String KR_IP_LIST_URL = "http://www.ipdeny.com/ipblocks/data/countries/kr.zone";
    private final List<CidrBlock> krCidrBlocks = new CopyOnWriteArrayList<>();
    private boolean isLoaded = false;

    @PostConstruct
    public void init() {
        refreshKrIpList();
    }

    // Refresh every 24 hours
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void refreshKrIpList() {
        try {
            log.info("Fetching KR IP list from: {}", KR_IP_LIST_URL);
            URL url = new URL(KR_IP_LIST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                List<CidrBlock> newBlocks = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (StringUtils.hasText(line)) {
                            newBlocks.add(new CidrBlock(line.trim()));
                        }
                    }
                }
                if (!newBlocks.isEmpty()) {
                    krCidrBlocks.clear();
                    krCidrBlocks.addAll(newBlocks);
                    isLoaded = true;
                    log.info("Loaded {} KR IP CIDR blocks.", krCidrBlocks.size());
                }
            } else {
                log.warn("Failed to fetch KR IP list. Status code: {}", conn.getResponseCode());
            }
        } catch (Exception e) {
            log.error("Error fetching KR IP list: {}", e.getMessage());
            // If failed initially and list is empty, add localhost/private IPs to allow
            // local dev at least
            if (!isLoaded) {
                log.warn("Using fallback local IP rules.");
            }
        }
    }

    public boolean isAllowed(String ip) {
        // Always allow localhost
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || ip.startsWith("192.168.")
                || ip.startsWith("10.")) {
            return true;
        }

        // If not loaded yet, fail open or closed?
        // Fail OPEN (Allow) to prevent blocking everyone if service fails?
        // OR Fail CLOSED? User wants to block foreign.
        // Let's safe-fail (Allow) for now, or the site becomes unusable if ipdeny is
        // down.
        if (!isLoaded || krCidrBlocks.isEmpty()) {
            return true;
        }

        long ipLong = ipToLong(ip);
        // Optimization: Could use Interval Tree, but for ~1000 blocks linear scan is
        // acceptable?
        // Actually KR blocks might be many. IPDeny KR zone is usually ~500-1000 lines.
        // Linear scan is fast enough.
        // For production, a specialized data structure is better.
        for (CidrBlock block : krCidrBlocks) {
            if (block.contains(ipLong)) {
                return true;
            }
        }
        return false;
    }

    private long ipToLong(String ipAddress) {
        // Basic IPv4 support only for this simple implementation
        // IPv6 ignored or checked separately? ipdeny list depends on source.
        // The list is usually IPv4.
        if (ipAddress.contains(":"))
            return 0; // Skip IPv6 for now logic

        String[] parts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= Integer.parseInt(parts[i]);
        }
        return result;
    }

    private static class CidrBlock {
        final long startIp;
        final long endIp;

        CidrBlock(String cidr) {
            String[] parts = cidr.split("/");
            String ip = parts[0];
            int prefix;
            if (parts.length < 2) {
                prefix = 32;
            } else {
                prefix = Integer.parseInt(parts[1]);
            }

            // Calculate start/end
            long ipLong = 0;
            String[] ipParts = ip.split("\\.");
            for (int i = 0; i < 4; i++) {
                ipLong <<= 8;
                ipLong |= Integer.parseInt(ipParts[i]);
            }

            long mask = 0xFFFFFFFFL << (32 - prefix);
            this.startIp = ipLong & mask;
            this.endIp = startIp | (~mask & 0xFFFFFFFFL); // strictly 32bit unsigned logic needed? Java long is 64bit so
                                                          // it's fine.
        }

        boolean contains(long ip) {
            return ip >= startIp && ip <= endIp;
        }
    }
}
