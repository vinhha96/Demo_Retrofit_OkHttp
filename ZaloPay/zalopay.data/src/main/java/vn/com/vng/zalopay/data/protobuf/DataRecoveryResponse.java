// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 94:1
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
import java.util.List;
import okio.ByteString;

/**
 * List of recovery message as result from client's request
 * Zalo Pay: 2.0
 */
public final class DataRecoveryResponse extends Message<DataRecoveryResponse, DataRecoveryResponse.Builder> {
  public static final ProtoAdapter<DataRecoveryResponse> ADAPTER = new ProtoAdapter_DataRecoveryResponse();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_STARTTIME = 0L;

  @WireField(
      tag = 1,
      adapter = "vn.com.vng.zalopay.data.protobuf.RecoveryMessage#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<RecoveryMessage> messages;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64",
      label = WireField.Label.REQUIRED
  )
  public final Long starttime;

  public DataRecoveryResponse(List<RecoveryMessage> messages, Long starttime) {
    this(messages, starttime, ByteString.EMPTY);
  }

  public DataRecoveryResponse(List<RecoveryMessage> messages, Long starttime, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.messages = Internal.immutableCopyOf("messages", messages);
    this.starttime = starttime;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.messages = Internal.copyOf("messages", messages);
    builder.starttime = starttime;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof DataRecoveryResponse)) return false;
    DataRecoveryResponse o = (DataRecoveryResponse) other;
    return unknownFields().equals(o.unknownFields())
        && messages.equals(o.messages)
        && starttime.equals(o.starttime);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + messages.hashCode();
      result = result * 37 + starttime.hashCode();
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (!messages.isEmpty()) builder.append(", messages=").append(messages);
    builder.append(", starttime=").append(starttime);
    return builder.replace(0, 2, "DataRecoveryResponse{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<DataRecoveryResponse, Builder> {
    public List<RecoveryMessage> messages;

    public Long starttime;

    public Builder() {
      messages = Internal.newMutableList();
    }

    public Builder messages(List<RecoveryMessage> messages) {
      Internal.checkElementsNotNull(messages);
      this.messages = messages;
      return this;
    }

    public Builder starttime(Long starttime) {
      this.starttime = starttime;
      return this;
    }

    @Override
    public DataRecoveryResponse build() {
      if (starttime == null) {
        throw Internal.missingRequiredFields(starttime, "starttime");
      }
      return new DataRecoveryResponse(messages, starttime, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_DataRecoveryResponse extends ProtoAdapter<DataRecoveryResponse> {
    ProtoAdapter_DataRecoveryResponse() {
      super(FieldEncoding.LENGTH_DELIMITED, DataRecoveryResponse.class);
    }

    @Override
    public int encodedSize(DataRecoveryResponse value) {
      return RecoveryMessage.ADAPTER.asRepeated().encodedSizeWithTag(1, value.messages)
          + ProtoAdapter.UINT64.encodedSizeWithTag(2, value.starttime)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, DataRecoveryResponse value) throws IOException {
      RecoveryMessage.ADAPTER.asRepeated().encodeWithTag(writer, 1, value.messages);
      ProtoAdapter.UINT64.encodeWithTag(writer, 2, value.starttime);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public DataRecoveryResponse decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.messages.add(RecoveryMessage.ADAPTER.decode(reader)); break;
          case 2: builder.starttime(ProtoAdapter.UINT64.decode(reader)); break;
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
    public DataRecoveryResponse redact(DataRecoveryResponse value) {
      Builder builder = value.newBuilder();
      Internal.redactElements(builder.messages, RecoveryMessage.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
