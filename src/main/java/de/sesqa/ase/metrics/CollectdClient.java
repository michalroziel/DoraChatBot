package de.sesqa.ase.metrics;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;


public class CollectdClient {

    private static final String COLLECTD_UNIXSOCK = "/var/run/collectd-unixsock";

    /*
        Auflösung von Collectd Variablen ähnlich zu Package namen:
        plugin.typeinstance.type.value
        Beispiel:
        dorachatbot.messages.totalcount.[VALUE]
     */
    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Socket path is intentionally hardcoded for collectd integration")
    public void sendMetric(String typeInstance, CollectdType type, long value) {
        File socketFile = new File(COLLECTD_UNIXSOCK);
        try (AFUNIXSocket socket = AFUNIXSocket.newInstance()) {
            socket.connect(new AFUNIXSocketAddress(socketFile));
            String metric = String.format("PUTVAL \"%s/%s-%s/%s\" interval=10 N:%d%n", InetAddress.getLocalHost().getHostName(), "DoraChatBot", typeInstance, type.getTypeName(), value);
            OutputStream os = socket.getOutputStream();
            os.write(metric.getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum CollectdType {
        GAUGE("gauge"), COUNTER("counter"), DERIVE("derive"), ABSOLUTE("absolute");

        private final String typeName;

        CollectdType(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

}