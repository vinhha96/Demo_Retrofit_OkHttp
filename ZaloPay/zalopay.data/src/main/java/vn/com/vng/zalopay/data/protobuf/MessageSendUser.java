// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 72:1
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
 * Notification message sent to user
 * Zalo Pay: 2.0
 */
public final class MessageSendUser extends Message<MessageSendUser, MessageSendUser.Builder> {
  public static final ProtoAdapter<MessageSendUser> ADAPTER = new ProtoAdapter_MessageSendUser();

  private static final long serialVersionUID = 0L;

  public static final ByteString DEFAULT_DATA = ByteString.EMPTY;

  public static final String DEFAULT_SIGNATURE = "";

  public static final Long DEFAULT_EXPIRETIME = 0L;

  public static final Long DEFAULT_USRID = 0L;

  public static final String DEFAULT_PUSHTITLE = "";

  public static final String DEFAULT_PUSHEMBEDDATA = "";

  public static final Integer DEFAULT_SOURCEID = 0;

  public static final Integer DEFAULT_MSGTYPE = 0;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#BYTES",
      label = WireField.Label.REQUIRED
  )
  public final ByteString data;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String signature;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#INT64"
  )
  public final Long expiretime;

  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64"
  )
  public final Long usrid;

  @WireField(
      tag = 5,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String pushtitle;

  @WireField(
      tag = 6,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String pushembeddata;

  @WireField(
      tag = 7,
      adapter = "com.squareup.wire.ProtoAdapter#UINT32"
  )
  public final Integer sourceid;

  @WireField(
      tag = 8,
      adapter = "com.squareup.wire.ProtoAdapter#UINT32"
  )
  public final Integer msgtype;

  public MessageSendUser(ByteString data, String signature, Long expiretime, Long usrid, String pushtitle, String pushembeddata, Integer sourceid, Integer msgtype) {
    this(data, signature, expiretime, usrid, pushtitle, pushembeddata, sourceid, msgtype, ByteString.EMPTY);
  }

  public MessageSendUser(ByteString data, String signature, Long expiretime, Long usrid, String pushtitle, String pushembeddata, Integer sourceid, Integer msgtype, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.data = data;
    this.signature = signature;
    this.expiretime = expiretime;
    this.usrid = usrid;
    this.pushtitle = pushtitle;
    this.pushembeddata = pushembeddata;
    this.sourceid = sourceid;
    this.msgtype = msgtype;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.data = data;
    builder.signature = signature;
    builder.expiretime = expiretime;
    builder.usrid = usrid;
    builder.pushtitle = pushtitle;
    builder.pushembeddata = pushembeddata;
    builder.sourceid = sourceid;
    builder.msgtype = msgtype;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof MessageSendUser)) return false;
    MessageSendUser o = (MessageSendUser) other;
    return unknownFields().equals(o.unknownFields())
        && data.equals(o.data)
        && signature.equals(o.signature)
        && Internal.equals(expiretime, o.expiretime)
        && Internal.equals(usrid, o.usrid)
        && Internal.equals(pushtitle, o.pushtitle)
        && Internal.equals(pushembeddata, o.pushembeddata)
        && Internal.equals(sourceid, o.sourceid)
        && Internal.equals(msgtype, o.msgtype);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + data.hashCode();
      result = result * 37 + signature.hashCode();
      result = result * 37 + (expiretime != null ? expiretime.hashCode() : 0);
      result = result * 37 + (usrid != null ? usrid.hashCode() : 0);
      result = result * 37 + (pushtitle != null ? pushtitle.hashCode() : 0);
      result = result * 37 + (pushembeddata != null ? pushembeddata.hashCode() : 0);
      result = result * 37 + (sourceid != null ? sourceid.hashCode() : 0);
      result = result * 37 + (msgtype != null ? msgtype.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", data=").append(data);
    builder.append(", signature=").append(signature);
    if (expiretime != null) builder.append(", expiretime=").append(expiretime);
    if (usrid != null) builder.append(", usrid=").append(usrid);
    if (pushtitle != null) builder.append(", pushtitle=").append(pushtitle);
    if (pushembeddata != null) builder.append(", pushembeddata=").append(pushembeddata);
    if (sourceid != null) builder.append(", sourceid=").append(sourceid);
    if (msgtype != null) builder.append(", msgtype=").append(msgtype);
    return builder.replace(0, 2, "MessageSendUser{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<MessageSendUser, Builder> {
    public ByteString data;

    public String signature;

    public Long expiretime;

    public Long usrid;

    public String pushtitle;

    public String pushembeddata;

    public Integer sourceid;

    public Integer msgtype;

    public Builder() {
    }

    public Builder data(ByteString data) {
      this.data = data;
      return this;
    }

    public Builder signature(String signature) {
      this.signature = signature;
      return this;
    }

    public Builder expiretime(Long expiretime) {
      this.expiretime = expiretime;
      return this;
    }

    public Builder usrid(Long usrid) {
      this.usrid = usrid;
      return this;
    }

    public Builder pushtitle(String pushtitle) {
      this.pushtitle = pushtitle;
      return this;
    }

    public Builder pushembeddata(String pushembeddata) {
      this.pushembeddata = pushembeddata;
      return this;
    }

    public Builder sourceid(Integer sourceid) {
      this.sourceid = sourceid;
      return this;
    }

    public Builder msgtype(Integer msgtype) {
      this.msgtype = msgtype;
      return this;
    }

    @Override
    public MessageSendUser build() {
      if (data == null
          || signature == null) {
        throw Internal.missingRequiredFields(data, "data",
            signature, "signature");
      }
      return new MessageSendUser(data, signature, expiretime, usrid, pushtitle, pushembeddata, sourceid, msgtype, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_MessageSendUser extends ProtoAdapter<MessageSendUser> {
    ProtoAdapter_MessageSendUser() {
      super(FieldEncoding.LENGTH_DELIMITED, MessageSendUser.class);
    }

    @Override
    public int encodedSize(MessageSendUser value) {
      return ProtoAdapter.BYTES.encodedSizeWithTag(1, value.data)
          + ProtoAdapter.STRING.encodedSizeWithTag(2, value.signature)
          + (value.expiretime != null ? ProtoAdapter.INT64.encodedSizeWithTag(3, value.expiretime) : 0)
          + (value.usrid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(4, value.usrid) : 0)
          + (value.pushtitle != null ? ProtoAdapter.STRING.encodedSizeWithTag(5, value.pushtitle) : 0)
          + (value.pushembeddata != null ? ProtoAdapter.STRING.encodedSizeWithTag(6, value.pushembeddata) : 0)
          + (value.sourceid != null ? ProtoAdapter.UINT32.encodedSizeWithTag(7, value.sourceid) : 0)
          + (value.msgtype != null ? ProtoAdapter.UINT32.encodedSizeWithTag(8, value.msgtype) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, MessageSendUser value) throws IOException {
      ProtoAdapter.BYTES.encodeWithTag(writer, 1, value.data);
      ProtoAdapter.STRING.encodeWithTag(writer, 2, value.signature);
      if (value.expiretime != null) ProtoAdapter.INT64.encodeWithTag(writer, 3, value.expiretime);
      if (value.usrid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 4, value.usrid);
      if (value.pushtitle != null) ProtoAdapter.STRING.encodeWithTag(writer, 5, value.pushtitle);
      if (value.pushembeddata != null) ProtoAdapter.STRING.encodeWithTag(writer, 6, value.pushembeddata);
      if (value.sourceid != null) ProtoAdapter.UINT32.encodeWithTag(writer, 7, value.sourceid);
      if (value.msgtype != null) ProtoAdapter.UINT32.encodeWithTag(writer, 8, value.msgtype);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public MessageSendUser decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.data(ProtoAdapter.BYTES.decode(reader)); break;
          case 2: builder.signature(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.expiretime(ProtoAdapter.INT64.decode(reader)); break;
          case 4: builder.usrid(ProtoAdapter.UINT64.decode(reader)); break;
          case 5: builder.pushtitle(ProtoAdapter.STRING.decode(reader)); break;
          case 6: builder.pushembeddata(ProtoAdapter.STRING.decode(reader)); break;
          case 7: builder.sourceid(ProtoAdapter.UINT32.decode(reader)); break;
          case 8: builder.msgtype(ProtoAdapter.UINT32.decode(reader)); break;
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
    public MessageSendUser redact(MessageSendUser value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
