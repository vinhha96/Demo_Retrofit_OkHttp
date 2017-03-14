// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 168:1
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
import okio.ByteString;

/**
 * Request message sent from client to server in order to retrieve recovery messages
 * Zalo Pay: 2.0
 */
public final class MessageRecoveryRequest extends Message<MessageRecoveryRequest, MessageRecoveryRequest.Builder> {
  public static final ProtoAdapter<MessageRecoveryRequest> ADAPTER = new ProtoAdapter_MessageRecoveryRequest();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_STARTTIME = 0L;

  public static final Integer DEFAULT_COUNT = 0;

  public static final Integer DEFAULT_ORDER = 0;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64",
      label = WireField.Label.REQUIRED
  )
  public final Long starttime;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#UINT32",
      label = WireField.Label.REQUIRED
  )
  public final Integer count;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  public final Integer order;

  public MessageRecoveryRequest(Long starttime, Integer count, Integer order) {
    this(starttime, count, order, ByteString.EMPTY);
  }

  public MessageRecoveryRequest(Long starttime, Integer count, Integer order, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.starttime = starttime;
    this.count = count;
    this.order = order;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.starttime = starttime;
    builder.count = count;
    builder.order = order;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof MessageRecoveryRequest)) return false;
    MessageRecoveryRequest o = (MessageRecoveryRequest) other;
    return unknownFields().equals(o.unknownFields())
        && starttime.equals(o.starttime)
        && count.equals(o.count)
        && Internal.equals(order, o.order);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + starttime.hashCode();
      result = result * 37 + count.hashCode();
      result = result * 37 + (order != null ? order.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", starttime=").append(starttime);
    builder.append(", count=").append(count);
    if (order != null) builder.append(", order=").append(order);
    return builder.replace(0, 2, "MessageRecoveryRequest{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<MessageRecoveryRequest, Builder> {
    public Long starttime;

    public Integer count;

    public Integer order;

    public Builder() {
    }

    public Builder starttime(Long starttime) {
      this.starttime = starttime;
      return this;
    }

    public Builder count(Integer count) {
      this.count = count;
      return this;
    }

    public Builder order(Integer order) {
      this.order = order;
      return this;
    }

    @Override
    public MessageRecoveryRequest build() {
      if (starttime == null
          || count == null) {
        throw Internal.missingRequiredFields(starttime, "starttime",
            count, "count");
      }
      return new MessageRecoveryRequest(starttime, count, order, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_MessageRecoveryRequest extends ProtoAdapter<MessageRecoveryRequest> {
    ProtoAdapter_MessageRecoveryRequest() {
      super(FieldEncoding.LENGTH_DELIMITED, MessageRecoveryRequest.class);
    }

    @Override
    public int encodedSize(MessageRecoveryRequest value) {
      return ProtoAdapter.UINT64.encodedSizeWithTag(1, value.starttime)
          + ProtoAdapter.UINT32.encodedSizeWithTag(2, value.count)
          + (value.order != null ? ProtoAdapter.INT32.encodedSizeWithTag(3, value.order) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, MessageRecoveryRequest value) throws IOException {
      ProtoAdapter.UINT64.encodeWithTag(writer, 1, value.starttime);
      ProtoAdapter.UINT32.encodeWithTag(writer, 2, value.count);
      if (value.order != null) ProtoAdapter.INT32.encodeWithTag(writer, 3, value.order);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public MessageRecoveryRequest decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.starttime(ProtoAdapter.UINT64.decode(reader)); break;
          case 2: builder.count(ProtoAdapter.UINT32.decode(reader)); break;
          case 3: builder.order(ProtoAdapter.INT32.decode(reader)); break;
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
    public MessageRecoveryRequest redact(MessageRecoveryRequest value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
