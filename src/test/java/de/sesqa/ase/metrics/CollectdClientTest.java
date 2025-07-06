package de.sesqa.ase.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import de.sesqa.ase.metrics.CollectdClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.SocketException;
import org.newsclub.net.unix.AFUNIXSocket;

public class CollectdClientTest {

    @Test
    @DisplayName("Gibt das tatsächliche Format von formatMetric aus")
    void printActualFormatMetricResult() throws Exception {
        CollectdClient client = new CollectdClient();
        Method method = CollectdClient.class.getDeclaredMethod("formatMetric", String.class, CollectdClient.CollectdType.class, long.class);
        method.setAccessible(true);
        String result = (String) method.invoke(client, "messages.totalcount", CollectdClient.CollectdType.GAUGE, 42L);
    }

    @Test
    @DisplayName("sendMetric successfully catches exception from connectToCollectdSocket")
    void sendMetricHandlesExceptionFromConnectToCollectdSocket() {
        CollectdClient client = new CollectdClient();
        assertDoesNotThrow(() ->
                client.sendMetric("test", CollectdClient.CollectdType.COUNTER, 1L)
        );
    }




}