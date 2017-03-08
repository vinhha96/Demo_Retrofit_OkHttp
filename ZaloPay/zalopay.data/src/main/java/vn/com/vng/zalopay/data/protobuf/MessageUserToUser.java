// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 122:1
package vn.com.vng.zalopay.data.protobuf;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;
import java.io.IOException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import okio.ByteString;

public final class MessageUserToUser extends Message<MessageUserToUser, MessageUserToUser.Builder> {
  public static final ProtoAdapter<MessageUserToUser> ADAPTER = new ProtoAdapter_MessageUserToUser();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_RECEIVERID = 0L;

  public static final ByteString DEFAULT_DATA = ByteString.EMPTY;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64",
      label = WireField.Label.REQUIRED
  )
  public final Long receiverid;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#BYTES"
  )
  public final ByteString data;

  public MessageUserToUser(Long receiverid, ByteString data) {
    this(receiverid, data, ByteString.EMPTY);
  }

  public MessageUserToUser(Long receiverid, ByteString data, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.receiverid = receiverid;
    this.data = data;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.receiverid = receiverid;
    builder.data = data;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof MessageUserToUser)) return false;
    MessageUserToUser o = (MessageUserToUser) other;
    return unknownFields().equals(o.unknownFields())
        && receiverid.equals(o.receiverid)
        && Internal.equals(data, o.data);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + receiverid.hashCode();
      result = result * 37 + (data != null ? data.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", receiverid=").append(receiverid);
    if (data != null) builder.append(", data=").append(data);
    return builder.replace(0, 2, "MessageUserToUser{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<MessageUserToUser, Builder> {
    public Long receiverid;

    public ByteString data;

    public Builder() {
    }

    public Builder receiverid(Long receiverid) {
      this.receiverid = receiverid;
      return this;
    }

    public Builder data(ByteString data) {
      this.data = data;
      return this;
    }

    @Override
    public MessageUserToUser build() {
      if (receiverid == null) {
        throw Internal.missingRequiredFields(receiverid, "receiverid");
      }
      return new MessageUserToUser(receiverid, data, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_MessageUserToUser extends ProtoAdapter<MessageUserToUser> {
    ProtoAdapter_MessageUserToUser() {
      super(FieldEncoding.LENGTH_DELIMITED, MessageUserToUser.class);
    }

    @Override
    public int encodedSize(MessageUserToUser value) {
      return ProtoAdapter.UINT64.encodedSizeWithTag(1, value.receiverid)
          + (value.data != null ? ProtoAdapter.BYTES.encodedSizeWithTag(2, value.data) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, MessageUserToUser value) throws IOException {
      ProtoAdapter.UINT64.encodeWithTag(writer, 1, value.receiverid);
      if (value.data != null) ProtoAdapter.BYTES.encodeWithTag(writer, 2, value.data);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public MessageUserToUser decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.receiverid(ProtoAdapter.UINT64.decode(reader)); break;
          case 2: builder.data(ProtoAdapter.BYTES.decode(reader)); break;
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
    public MessageUserToUser redact(MessageUserToUser value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
