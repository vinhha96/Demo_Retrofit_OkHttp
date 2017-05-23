// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 68:1
package vn.com.vng.zalopay.data.protobuf;

import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.WireEnum;
import java.lang.Override;

/**
 * Define the response code from Payment Connector
 * Zalo Pay: 2.12
 */
public enum PaymentCode implements WireEnum {
  PAY_UNKNOWN(0),

  PAY_SUCCESS(1),

  PAY_WRONG_FORMAT(2),

  PAY_MISS_DATA(3),

  PAY_SEND_REQ_FAILED(4),

  PAY_TIMEOUT(5),

  PAY_EXCEPTION(6);

  public static final ProtoAdapter<PaymentCode> ADAPTER = ProtoAdapter.newEnumAdapter(PaymentCode.class);

  private final int value;

  PaymentCode(int value) {
    this.value = value;
  }

  /**
   * Return the constant for {@code value} or null.
   */
  public static PaymentCode fromValue(int value) {
    switch (value) {
      case 0: return PAY_UNKNOWN;
      case 1: return PAY_SUCCESS;
      case 2: return PAY_WRONG_FORMAT;
      case 3: return PAY_MISS_DATA;
      case 4: return PAY_SEND_REQ_FAILED;
      case 5: return PAY_TIMEOUT;
      case 6: return PAY_EXCEPTION;
      default: return null;
    }
  }

  @Override
  public int getValue() {
    return value;
  }
}
