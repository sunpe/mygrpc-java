package com.sunpe.mygrpc.base.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class IpUtil {
    private static final String IPV4_KEY = "_v4";
    private static final String IPV6_KEY = "_v6";

    private static final String LINUX_PREFERRED_INTERFACE = "eth";
    private static final String DARWIN_PREFERRED_INTERFACE = "en";

    private static volatile String localIpV4Address;
    private static volatile String localIpV6Address;

    private static final Map<String, InetAddress> allInterfaceAddresses = new HashMap<>();

    static {
        try {
            getAddressFromNetworkInterface();
        } catch (Exception e) {
            // todo
        }
    }

    public static String getLocalHostName() {
        String hostName = tryGetHostNameFromNetworkInterface(true);
        if (hostName == null) {
            hostName = tryGetHostNameFromNetworkInterface(false);
        }
        return hostName;
    }

    public static String getLocalIpV4Address() {
        if (localIpV4Address != null && !localIpV4Address.isEmpty()) {
            return localIpV4Address;
        }
        String ip = tryGetHostNameFromNetworkInterface(true);
        if (ip != null && !ip.isEmpty()) {
            localIpV4Address = ip;
            return ip;
        }
        ip = tryGetLocalAddress();
        localIpV4Address = ip;
        return ip;
    }

    public static String getLocalIpV6Address() {
        if (localIpV6Address != null && !localIpV6Address.isEmpty()) {
            return localIpV6Address;
        }
        String ip = tryGetHostNameFromNetworkInterface(false);
        if (ip != null && !ip.isEmpty()) {
            localIpV6Address = ip;
        }
        return ip;
    }

    private static String tryGetLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // todo
        }
        return "127.0.0.1";
    }

    private static String tryGetHostNameFromNetworkInterface(boolean ipV4) {
        InetAddress inetAddress = tryGetAddressFromInterfaceAddress(ipV4);
        return inetAddress != null ? inetAddress.getHostName() : null;
    }

    private static String tryGetIpFromNetworkInterface(boolean ipV4) {
        InetAddress inetAddress = tryGetAddressFromInterfaceAddress(ipV4);
        return inetAddress != null ? inetAddress.getHostAddress() : null;
    }

    private static InetAddress tryGetAddressFromInterfaceAddress(boolean ipV4) {
        for (Map.Entry<String, InetAddress> entry : allInterfaceAddresses.entrySet()) {
            if (ipV4 && entry.getKey().endsWith(IPV4_KEY)) {
                if (entry.getKey().startsWith(LINUX_PREFERRED_INTERFACE) || entry.getKey().startsWith(DARWIN_PREFERRED_INTERFACE)) {
                    return entry.getValue();
                }
            }
            if (!ipV4 && entry.getKey().endsWith(IPV6_KEY)) {
                if (entry.getKey().startsWith(LINUX_PREFERRED_INTERFACE) || entry.getKey().startsWith(DARWIN_PREFERRED_INTERFACE)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private static void getAddressFromNetworkInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface network = interfaces.nextElement();
            if (!network.isUp() || network.isLoopback() || network.isVirtual()) {
                continue;
            }
            Enumeration<InetAddress> addresses = network.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (isLocalAddress(address)) {
                    continue;
                }
                if (address instanceof Inet4Address) {
                    allInterfaceAddresses.put(network.getName() + IPV4_KEY, address);
                }
                if (address instanceof Inet6Address) {
                    allInterfaceAddresses.put(network.getName() + IPV6_KEY, address);
                }
            }
        }
    }

    private static boolean isLocalAddress(InetAddress address) {
        return address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isMulticastAddress();
    }
}
