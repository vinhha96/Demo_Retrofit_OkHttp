// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 211:1
package vn.com.vng.zalopay.data.protobuf;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import okio.ByteString;

/**
 * Request message that client sends to Payment Connector
 * Zalo Pay: 2.12
 */
public final class PaymentRequestMessage extends Message<PaymentRequestMessage, PaymentRequestMessage.Builder> {
  public static final ProtoAdapter<PaymentRequestMessage> ADAPTER = new ProtoAdapter_PaymentRequestMessage();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_REQUESTID = 0L;

  public static final String DEFAULT_DOMAIN = "";

  public static final String DEFAULT_METHOD = "";

  public static final Integer DEFAULT_PORT = 0;

  public static final String DEFAULT_PATH = "";

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64"
  )
  public final Long requestid;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String domain;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String method;

  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#UINT32"
  )
  public final Integer port;

  @WireField(
      tag = 5,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String path;

  @WireField(
      tag = 6,
      adapter = "vn.com.vng.zalopay.data.protobuf.NameValuePair#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<NameValuePair> params;

  @WireField(
      tag = 7,
      adapter = "vn.com.vng.zalopay.data.protobuf.NameValuePair#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<NameValuePair> headers;

  public PaymentRequestMessage(Long requestid, String domain, String method, Integer port, String path, List<NameValuePair> params, List<NameValuePair> headers) {
    this(requestid, domain, method, port, path, params, headers, ByteString.EMPTY);
  }

  public PaymentRequestMessage(Long requestid, String domain, String method, Integer port, String path, List<NameValuePair> params, List<NameValuePair> headers, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.requestid = requestid;
    this.domain = domain;
    this.method = method;
    this.port = port;
    this.path = path;
    this.params = Internal.immutableCopyOf("params", params);
    this.headers = Internal.immutableCopyOf("headers", headers);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.requestid = requestid;
    builder.domain = domain;
    builder.method = method;
    builder.port = port;
    builder.path = path;
    builder.params = Internal.copyOf("params", params);
    builder.headers = Internal.copyOf("headers", headers);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof PaymentRequestMessage)) return false;
    PaymentRequestMessage o = (PaymentRequestMessage) other;
    return unknownFields().equals(o.unknownFields())
        && Internal.equals(requestid, o.requestid)
        && Internal.equals(domain, o.domain)
        && Internal.equals(method, o.method)
        && Internal.equals(port, o.port)
        && Internal.equals(path, o.path)
        && params.equals(o.params)
        && headers.equals(o.headers);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (requestid != null ? requestid.hashCode() : 0);
      result = result * 37 + (domain != null ? domain.hashCode() : 0);
      result = result * 37 + (method != null ? method.hashCode() : 0);
      result = result * 37 + (port != null ? port.hashCode() : 0);
      result = result * 37 + (path != null ? path.hashCode() : 0);
      result = result * 37 + params.hashCode();
      result = result * 37 + headers.hashCode();
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (requestid != null) builder.append(", requestid=").append(requestid);
    if (domain != null) builder.append(", domain=").append(domain);
    if (method != null) builder.append(", method=").append(method);
    if (port != null) builder.append(", port=").append(port);
    if (path != null) builder.append(", path=").append(path);
    if (!params.isEmpty()) builder.append(", params=").append(params);
    if (!headers.isEmpty()) builder.append(", headers=").append(headers);
    return builder.replace(0, 2, "PaymentRequestMessage{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<PaymentRequestMessage, Builder> {
    public Long requestid;

    public String domain;

    public String method;

    public Integer port;

    public String path;

    public List<NameValuePair> params;

    public List<NameValuePair> headers;

    public Builder() {
      params = Internal.newMutableList();
      headers = Internal.newMutableList();
    }

    public Builder requestid(Long requestid) {
      this.requestid = requestid;
      return this;
    }

    public Builder domain(String domain) {
      this.domain = domain;
      return this;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder port(Integer port) {
      this.port = port;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder params(List<NameValuePair> params) {
      Internal.checkElementsNotNull(params);
      this.params = params;
      return this;
    }

    public Builder headers(List<NameValuePair> headers) {
      Internal.checkElementsNotNull(headers);
      this.headers = headers;
      return this;
    }

    @Override
    public PaymentRequestMessage build() {
      return new PaymentRequestMessage(requestid, domain, method, port, path, params, headers, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_PaymentRequestMessage extends ProtoAdapter<PaymentRequestMessage> {
    ProtoAdapter_PaymentRequestMessage() {
      super(FieldEncoding.LENGTH_DELIMITED, PaymentRequestMessage.class);
    }

    @Override
    public int encodedSize(PaymentRequestMessage value) {
      return (value.requestid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(1, value.requestid) : 0)
          + (value.domain != null ? ProtoAdapter.STRING.encodedSizeWithTag(2, value.domain) : 0)
          + (value.method != null ? ProtoAdapter.STRING.encodedSizeWithTag(3, value.method) : 0)
          + (value.port != null ? ProtoAdapter.UINT32.encodedSizeWithTag(4, value.port) : 0)
          + (value.path != null ? ProtoAdapter.STRING.encodedSizeWithTag(5, value.path) : 0)
          + NameValuePair.ADAPTER.asRepeated().encodedSizeWithTag(6, value.params)
          + NameValuePair.ADAPTER.asRepeated().encodedSizeWithTag(7, value.headers)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, PaymentRequestMessage value) throws IOException {
      if (value.requestid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 1, value.requestid);
      if (value.domain != null) ProtoAdapter.STRING.encodeWithTag(writer, 2, value.domain);
      if (value.method != null) ProtoAdapter.STRING.encodeWithTag(writer, 3, value.method);
      if (value.port != null) ProtoAdapter.UINT32.encodeWithTag(writer, 4, value.port);
      if (value.path != null) ProtoAdapter.STRING.encodeWithTag(writer, 5, value.path);
      NameValuePair.ADAPTER.asRepeated().encodeWithTag(writer, 6, value.params);
      NameValuePair.ADAPTER.asRepeated().encodeWithTag(writer, 7, value.headers);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public PaymentRequestMessage decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.requestid(ProtoAdapter.UINT64.decode(reader)); break;
          case 2: builder.domain(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.method(ProtoAdapter.STRING.decode(reader)); break;
          case 4: builder.port(ProtoAdapter.UINT32.decode(reader)); break;
          case 5: builder.path(ProtoAdapter.STRING.decode(reader)); break;
          case 6: builder.params.add(NameValuePair.ADAPTER.decode(reader)); break;
          case 7: builder.headers.add(NameValuePair.ADAPTER.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public PaymentRequestMessage redact(PaymentRequestMessage value) {
      Builder builder = value.newBuilder();
      Internal.redactElements(builder.params, NameValuePair.ADAPTER);
      Internal.redactElements(builder.headers, NameValuePair.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
