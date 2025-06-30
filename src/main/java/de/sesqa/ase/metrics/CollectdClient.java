package de.sesqa.ase.metrics;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class CollectdClient {
    private final String host;
    private final int port;

    public CollectdClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /*
        Auflösung von Collectd Variablen ähnlich zu Package namen:
        plugin.typeinstance.type.value
        Beispiel:
        dorachatbot.messages.totalcount.[VALUE]
     */

    public void sendMetric(String typeInstance, String type, long value) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String metric = String.format("PUTVAL \"%s/%s-%s/%s\" interval=10 N:%d%n",
                    InetAddress.getLocalHost().getHostName(), "DoraChatBot", typeInstance, type, value);
            byte[] data = metric.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}