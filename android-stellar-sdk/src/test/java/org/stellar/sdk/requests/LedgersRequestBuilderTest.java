package org.stellar.sdk.requests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.stellar.sdk.Server;

import java.net.URI;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class LedgersRequestBuilderTest {
  @Test
  public void testAccounts() {
    Server server = new Server("https://horizon-testnet.stellar.org");
    URI uri = server.ledgers()
            .limit(200)
            .order(RequestBuilder.Order.ASC)
            .buildUri();
    assertEquals("https://horizon-testnet.stellar.org/ledgers?limit=200&order=asc", uri.toString());
  }
}
