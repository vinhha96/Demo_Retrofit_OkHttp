// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 47:1
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

public final class ResultAuth extends Message<ResultAuth, ResultAuth.Builder> {
  public static final ProtoAdapter<ResultAuth> ADAPTER = new ProtoAdapter_ResultAuth();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_USRID = 0L;

  public static final Integer DEFAULT_RESULT = 0;

  public static final Integer DEFAULT_CODE = 0;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#UINT64",
      label = WireField.Label.REQUIRED
  )
  public final Long usrid;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#INT32",
      label = WireField.Label.REQUIRED
  )
  public final Integer result;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  public final Integer code;

  public ResultAuth(Long usrid, Integer result, Integer code) {
    this(usrid, result, code, ByteString.EMPTY);
  }

  public ResultAuth(Long usrid, Integer result, Integer code, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.usrid = usrid;
    this.result = result;
    this.code = code;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.usrid = usrid;
    builder.result = result;
    builder.code = code;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ResultAuth)) return false;
    ResultAuth o = (ResultAuth) other;
    return unknownFields().equals(o.unknownFields())
        && usrid.equals(o.usrid)
        && result.equals(o.result)
        && Internal.equals(code, o.code);
  }

  @Override
  public int hashCode() {
    int result_ = super.hashCode;
    if (result_ == 0) {
      result_ = unknownFields().hashCode();
      result_ = result_ * 37 + usrid.hashCode();
      result_ = result_ * 37 + result.hashCode();
      result_ = result_ * 37 + (code != null ? code.hashCode() : 0);
      super.hashCode = result_;
    }
    return result_;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", usrid=").append(usrid);
    builder.append(", result=").append(result);
    if (code != null) builder.append(", code=").append(code);
    return builder.replace(0, 2, "ResultAuth{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<ResultAuth, Builder> {
    public Long usrid;

    public Integer result;

    public Integer code;

    public Builder() {
    }

    public Builder usrid(Long usrid) {
      this.usrid = usrid;
      return this;
    }

    public Builder result(Integer result) {
      this.result = result;
      return this;
    }

    public Builder code(Integer code) {
      this.code = code;
      return this;
    }

    @Override
    public ResultAuth build() {
      if (usrid == null
          || result == null) {
        throw Internal.missingRequiredFields(usrid, "usrid",
            result, "result");
      }
      return new ResultAuth(usrid, result, code, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_ResultAuth extends ProtoAdapter<ResultAuth> {
    ProtoAdapter_ResultAuth() {
      super(FieldEncoding.LENGTH_DELIMITED, ResultAuth.class);
    }

    @Override
    public int encodedSize(ResultAuth value) {
      return ProtoAdapter.UINT64.encodedSizeWithTag(1, value.usrid)
          + ProtoAdapter.INT32.encodedSizeWithTag(2, value.result)
          + (value.code != null ? ProtoAdapter.INT32.encodedSizeWithTag(3, value.code) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, ResultAuth value) throws IOException {
      ProtoAdapter.UINT64.encodeWithTag(writer, 1, value.usrid);
      ProtoAdapter.INT32.encodeWithTag(writer, 2, value.result);
      if (value.code != null) ProtoAdapter.INT32.encodeWithTag(writer, 3, value.code);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public ResultAuth decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.usrid(ProtoAdapter.UINT64.decode(reader)); break;
          case 2: builder.result(ProtoAdapter.INT32.decode(reader)); break;
          case 3: builder.code(ProtoAdapter.INT32.decode(reader)); break;
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
    public ResultAuth redact(ResultAuth value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
