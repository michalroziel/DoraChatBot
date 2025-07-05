package de.sesqa.ase.metrics;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client for sending metrics to a collectd daemon via a UNIX socket. This class handles the
 * connection, formatting, and sending of metric data according to the collectd plain text protocol.
 */
@SuppressFBWarnings(
    value = {"DMI_HARDCODED_ABSOLUTE_FILENAME"},
    justification =
        "Socket path is intentionally hardcoded"
            + " for collectd integration; read result is not needed")
public class CollectdClient {

  private static final Logger logger = LoggerFactory.getLogger(CollectdClient.class);

  /** The hardcoded path to the collectd UNIX socket file. */
  private static final String COLLECTD_UNIXSOCK = "/var/run/collectd-unixsock";

  /*
     Auflösung von Collectd Variablen ähnlich zu Package namen:
     plugin.typeinstance.type.value
     Beispiel:
     dorachatbot.messages.totalcount.[VALUE]
  */

  /**
   * Sends a metric to the collectd daemon. It establishes a connection, formats the metric, sends
   * it, and reads the response.
   *
   * @param typeInstance The instance of the plugin/type (e.g., "messages.totalcount").
   * @param type The type of the metric (e.g., GAUGE, COUNTER).
   * @param value The numeric value of the metric.
   */
  public void sendMetric(String typeInstance, CollectdType type, long value) {
    File socketFile = new File(COLLECTD_UNIXSOCK);

    try (AFUNIXSocket socket = connectToCollectdSocket(socketFile);
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream(); ) {
      String metric = formatMetric(typeInstance, type, value);
      logMetric(metric);
      writeMetricToSocket(metric, os);
      readCollectdResponse(is);
    } catch (Exception e) {
      logger.error("Failed to send metric to collectd" + e.getMessage());
    }
  }

  /**
   * Connects to the collectd UNIX socket.
   *
   * @param socketFile The file representing the UNIX socket.
   * @return An established AFUNIXSocket connection.
   * @throws IOException if an I/O error occurs when creating or connecting the socket.
   */
  private AFUNIXSocket connectToCollectdSocket(File socketFile) throws IOException {
    AFUNIXSocket socket = AFUNIXSocket.newInstance();
    socket.connect(new AFUNIXSocketAddress(socketFile));

    return socket;
  }

  /**
   * Formats the metric data into a string that collectd can understand via the PUTVAL command.
   *
   * @param typeInstance The specific instance for the metric.
   * @param type The collectd type of the metric.
   * @param value The value of the metric.
   * @return A formatted string for the PUTVAL command.
   */
  private String formatMetric(String typeInstance, CollectdType type, long value) {
    return String.format(
        "PUTVAL \"%s/%s/%s\" interval=10 N:%d%n",
        "dorachatbot", typeInstance, type.getTypeName(), value);
  }

  /**
   * Logs the metric string in both human-readable and byte array format for debugging.
   *
   * @param metric The formatted metric string to log.
   */
  private void logMetric(String metric) {
    logger.info("Sending (Human): " + metric);
    logger.info("Sending (Bytes): " + Arrays.toString(metric.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Writes the formatted metric string to the provided OutputStream of the socket.
   *
   * @param metric The metric string to send.
   * @param os The output stream of the connected socket.
   * @throws IOException if an I/O error occurs when writing to the stream.
   */
  private void writeMetricToSocket(String metric, OutputStream os) throws IOException {
    os.write(metric.getBytes(StandardCharsets.UTF_8));
    os.flush();
  }

  /**
   * Reads the response from the collectd daemon from the socket's InputStream and logs it.
   *
   * @param is The input stream of the connected socket.
   * @throws IOException if an I/O error occurs when reading from the stream.
   */
  private void readCollectdResponse(InputStream is) throws IOException {
    // Lies die Antwort von collectd (optional: Buffer und Timeout setzen)
    byte[] buffer = new byte[1024];
    int bytesRead = is.read(buffer); // blockiert bis collectd antwortet oder EOF

    if (bytesRead != -1) {
      String response = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
      logger.info("Collectd responded: " + response);
    }
  }

  /** Represents the types of metrics that can be sent to collectd. */
  public enum CollectdType {
    /** A gauge value is for storing values that can increase or decrease, like temperature. */
    GAUGE("gauge"),
    /**
     * A counter is for values that only increase, like the number of requests. It will be reset on
     * overflow.
     */
    COUNTER("counter"),
    /**
     * A derive value is stored as a rate of change. It's like a counter but for non-monotonic
     * values.
     */
    DERIVE("derive"),
    /** An absolute value is for counters that are reset upon reading. */
    ABSOLUTE("absolute");

    private final String typeName;

    /**
     * Constructs a CollectdType enum constant.
     *
     * @param typeName The string representation of the type used by collectd.
     */
    CollectdType(String typeName) {
      this.typeName = typeName;
    }

    /**
     * Gets the string name of the metric type.
     *
     * @return The name of the type as a string.
     */
    public String getTypeName() {
      return typeName;
    }
  }
}
