// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 49:1
package vn.com.vng.zalopay.data.ws.protobuf;

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

public final class RecoveryMessage extends Message<RecoveryMessage, RecoveryMessage.Builder> {
  public static final ProtoAdapter<RecoveryMessage> ADAPTER = new ProtoAdapter_RecoveryMessage();

  private static final long serialVersionUID = 0L;

  public static final ByteString DEFAULT_DATA = ByteString.EMPTY;

  public static final Integer DEFAULT_STATUS = 0;

  public static final Long DEFAULT_MTAID = 0L;

  public static final Long DEFAULT_MTUID = 0L;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#BYTES",
      label = WireField.Label.REQUIRED
  )
  public final ByteString data;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  public final Integer status;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64"
  )
  public final Long mtaid;

  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64"
  )
  public final Long mtuid;

  public RecoveryMessage(ByteString data, Integer status, Long mtaid, Long mtuid) {
    this(data, status, mtaid, mtuid, ByteString.EMPTY);
  }

  public RecoveryMessage(ByteString data, Integer status, Long mtaid, Long mtuid, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.data = data;
    this.status = status;
    this.mtaid = mtaid;
    this.mtuid = mtuid;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.data = data;
    builder.status = status;
    builder.mtaid = mtaid;
    builder.mtuid = mtuid;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof RecoveryMessage)) return false;
    RecoveryMessage o = (RecoveryMessage) other;
    return unknownFields().equals(o.unknownFields())
        && data.equals(o.data)
        && Internal.equals(status, o.status)
        && Internal.equals(mtaid, o.mtaid)
        && Internal.equals(mtuid, o.mtuid);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + data.hashCode();
      result = result * 37 + (status != null ? status.hashCode() : 0);
      result = result * 37 + (mtaid != null ? mtaid.hashCode() : 0);
      result = result * 37 + (mtuid != null ? mtuid.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", data=").append(data);
    if (status != null) builder.append(", status=").append(status);
    if (mtaid != null) builder.append(", mtaid=").append(mtaid);
    if (mtuid != null) builder.append(", mtuid=").append(mtuid);
    return builder.replace(0, 2, "RecoveryMessage{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<RecoveryMessage, Builder> {
    public ByteString data;

    public Integer status;

    public Long mtaid;

    public Long mtuid;

    public Builder() {
    }

    public Builder data(ByteString data) {
      this.data = data;
      return this;
    }

    public Builder status(Integer status) {
      this.status = status;
      return this;
    }

    public Builder mtaid(Long mtaid) {
      this.mtaid = mtaid;
      return this;
    }

    public Builder mtuid(Long mtuid) {
      this.mtuid = mtuid;
      return this;
    }

    @Override
    public RecoveryMessage build() {
      if (data == null) {
        throw Internal.missingRequiredFields(data, "data");
      }
      return new RecoveryMessage(data, status, mtaid, mtuid, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_RecoveryMessage extends ProtoAdapter<RecoveryMessage> {
    ProtoAdapter_RecoveryMessage() {
      super(FieldEncoding.LENGTH_DELIMITED, RecoveryMessage.class);
    }

    @Override
    public int encodedSize(RecoveryMessage value) {
      return ProtoAdapter.BYTES.encodedSizeWithTag(1, value.data)
          + (value.status != null ? ProtoAdapter.INT32.encodedSizeWithTag(2, value.status) : 0)
          + (value.mtaid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(3, value.mtaid) : 0)
          + (value.mtuid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(4, value.mtuid) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, RecoveryMessage value) throws IOException {
      ProtoAdapter.BYTES.encodeWithTag(writer, 1, value.data);
      if (value.status != null) ProtoAdapter.INT32.encodeWithTag(writer, 2, value.status);
      if (value.mtaid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 3, value.mtaid);
      if (value.mtuid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 4, value.mtuid);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public RecoveryMessage decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.data(ProtoAdapter.BYTES.decode(reader)); break;
          case 2: builder.status(ProtoAdapter.INT32.decode(reader)); break;
          case 3: builder.mtaid(ProtoAdapter.UINT64.decode(reader)); break;
          case 4: builder.mtuid(ProtoAdapter.UINT64.decode(reader)); break;
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
    public RecoveryMessage redact(RecoveryMessage value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
