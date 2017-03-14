// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 176:1
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
 * Zalo Pay: 2.0
 */
public final class UpdateStatus extends Message<UpdateStatus, UpdateStatus.Builder> {
  public static final ProtoAdapter<UpdateStatus> ADAPTER = new ProtoAdapter_UpdateStatus();

  private static final long serialVersionUID = 0L;

  public static final Integer DEFAULT_STATUS = 0;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#INT32",
      label = WireField.Label.REQUIRED
  )
  public final Integer status;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64",
      label = WireField.Label.REPEATED
  )
  public final List<Long> msgids;

  public UpdateStatus(Integer status, List<Long> msgids) {
    this(status, msgids, ByteString.EMPTY);
  }

  public UpdateStatus(Integer status, List<Long> msgids, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.status = status;
    this.msgids = Internal.immutableCopyOf("msgids", msgids);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.status = status;
    builder.msgids = Internal.copyOf("msgids", msgids);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof UpdateStatus)) return false;
    UpdateStatus o = (UpdateStatus) other;
    return unknownFields().equals(o.unknownFields())
        && status.equals(o.status)
        && msgids.equals(o.msgids);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + status.hashCode();
      result = result * 37 + msgids.hashCode();
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", status=").append(status);
    if (!msgids.isEmpty()) builder.append(", msgids=").append(msgids);
    return builder.replace(0, 2, "UpdateStatus{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<UpdateStatus, Builder> {
    public Integer status;

    public List<Long> msgids;

    public Builder() {
      msgids = Internal.newMutableList();
    }

    public Builder status(Integer status) {
      this.status = status;
      return this;
    }

    public Builder msgids(List<Long> msgids) {
      Internal.checkElementsNotNull(msgids);
      this.msgids = msgids;
      return this;
    }

    @Override
    public UpdateStatus build() {
      if (status == null) {
        throw Internal.missingRequiredFields(status, "status");
      }
      return new UpdateStatus(status, msgids, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_UpdateStatus extends ProtoAdapter<UpdateStatus> {
    ProtoAdapter_UpdateStatus() {
      super(FieldEncoding.LENGTH_DELIMITED, UpdateStatus.class);
    }

    @Override
    public int encodedSize(UpdateStatus value) {
      return ProtoAdapter.INT32.encodedSizeWithTag(1, value.status)
          + ProtoAdapter.UINT64.asRepeated().encodedSizeWithTag(2, value.msgids)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, UpdateStatus value) throws IOException {
      ProtoAdapter.INT32.encodeWithTag(writer, 1, value.status);
      ProtoAdapter.UINT64.asRepeated().encodeWithTag(writer, 2, value.msgids);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public UpdateStatus decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.status(ProtoAdapter.INT32.decode(reader)); break;
          case 2: builder.msgids.add(ProtoAdapter.UINT64.decode(reader)); break;
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
    public UpdateStatus redact(UpdateStatus value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
