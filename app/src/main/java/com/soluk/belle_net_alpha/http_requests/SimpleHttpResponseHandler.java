package com.soluk.belle_net_alpha.http_requests;

/**
 * HTTP response handler.
 * @author <a href="https://github.com/joecheatham">joecheatham</a>
 * @version 1.0
 */
public interface SimpleHttpResponseHandler
{
  public void onResponse(int responseCode, String responseBody);
}
