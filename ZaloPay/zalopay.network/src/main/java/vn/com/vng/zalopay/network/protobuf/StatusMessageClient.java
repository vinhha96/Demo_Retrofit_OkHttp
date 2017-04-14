// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 153:1
package vn.com.vng.zalopay.network.protobuf;

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
 * Zalo Pay: 2.0
 */
public final class StatusMessageClient extends Message<StatusMessageClient, StatusMessageClient.Builder> {
  public static final ProtoAdapter<StatusMessageClient> ADAPTER = new ProtoAdapter_StatusMessageClient();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_USERID = 0L;

  public static final Integer DEFAULT_STATUS = 0;

  public static final Long DEFAULT_MTAID = 0L;

  public static final Long DEFAULT_MTUID = 0L;

  public static final Integer DEFAULT_SOURCEID = 0;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64"
  )
  public final Long userid;

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

  @WireField(
      tag = 5,
      adapter = "com.squareup.wire.ProtoAdapter#UINT32"
  )
  public final Integer sourceid;

  public StatusMessageClient(Long userid, Integer status, Long mtaid, Long mtuid, Integer sourceid) {
    this(userid, status, mtaid, mtuid, sourceid, ByteString.EMPTY);
  }

  public StatusMessageClient(Long userid, Integer status, Long mtaid, Long mtuid, Integer sourceid, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.userid = userid;
    this.status = status;
    this.mtaid = mtaid;
    this.mtuid = mtuid;
    this.sourceid = sourceid;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.userid = userid;
    builder.status = status;
    builder.mtaid = mtaid;
    builder.mtuid = mtuid;
    builder.sourceid = sourceid;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof StatusMessageClient)) return false;
    StatusMessageClient o = (StatusMessageClient) other;
    return unknownFields().equals(o.unknownFields())
        && Internal.equals(userid, o.userid)
        && Internal.equals(status, o.status)
        && Internal.equals(mtaid, o.mtaid)
        && Internal.equals(mtuid, o.mtuid)
        && Internal.equals(sourceid, o.sourceid);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (userid != null ? userid.hashCode() : 0);
      result = result * 37 + (status != null ? status.hashCode() : 0);
      result = result * 37 + (mtaid != null ? mtaid.hashCode() : 0);
      result = result * 37 + (mtuid != null ? mtuid.hashCode() : 0);
      result = result * 37 + (sourceid != null ? sourceid.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (userid != null) builder.append(", userid=").append(userid);
    if (status != null) builder.append(", status=").append(status);
    if (mtaid != null) builder.append(", mtaid=").append(mtaid);
    if (mtuid != null) builder.append(", mtuid=").append(mtuid);
    if (sourceid != null) builder.append(", sourceid=").append(sourceid);
    return builder.replace(0, 2, "StatusMessageClient{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<StatusMessageClient, Builder> {
    public Long userid;

    public Integer status;

    public Long mtaid;

    public Long mtuid;

    public Integer sourceid;

    public Builder() {
    }

    public Builder userid(Long userid) {
      this.userid = userid;
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

    public Builder sourceid(Integer sourceid) {
      this.sourceid = sourceid;
      return this;
    }

    @Override
    public StatusMessageClient build() {
      return new StatusMessageClient(userid, status, mtaid, mtuid, sourceid, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_StatusMessageClient extends ProtoAdapter<StatusMessageClient> {
    ProtoAdapter_StatusMessageClient() {
      super(FieldEncoding.LENGTH_DELIMITED, StatusMessageClient.class);
    }

    @Override
    public int encodedSize(StatusMessageClient value) {
      return (value.userid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(1, value.userid) : 0)
          + (value.status != null ? ProtoAdapter.INT32.encodedSizeWithTag(2, value.status) : 0)
          + (value.mtaid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(3, value.mtaid) : 0)
          + (value.mtuid != null ? ProtoAdapter.UINT64.encodedSizeWithTag(4, value.mtuid) : 0)
          + (value.sourceid != null ? ProtoAdapter.UINT32.encodedSizeWithTag(5, value.sourceid) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, StatusMessageClient value) throws IOException {
      if (value.userid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 1, value.userid);
      if (value.status != null) ProtoAdapter.INT32.encodeWithTag(writer, 2, value.status);
      if (value.mtaid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 3, value.mtaid);
      if (value.mtuid != null) ProtoAdapter.UINT64.encodeWithTag(writer, 4, value.mtuid);
      if (value.sourceid != null) ProtoAdapter.UINT32.encodeWithTag(writer, 5, value.sourceid);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public StatusMessageClient decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.userid(ProtoAdapter.UINT64.decode(reader)); break;
          case 2: builder.status(ProtoAdapter.INT32.decode(reader)); break;
          case 3: builder.mtaid(ProtoAdapter.UINT64.decode(reader)); break;
          case 4: builder.mtuid(ProtoAdapter.UINT64.decode(reader)); break;
          case 5: builder.sourceid(ProtoAdapter.UINT32.decode(reader)); break;
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
    public StatusMessageClient redact(StatusMessageClient value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
