// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: zpmsguser.proto at 61:1
package vn.com.vng.zalopay.network.protobuf;

import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.WireEnum;
import java.lang.Override;

/**
 * Define the response code from Payment Connector
 * Zalo Pay: 2.10
 */
public enum PaymentCode implements WireEnum {
  PAY_SUCCESS(0),

  PAY_WRONG_FORMAT(1),

  PAY_MISS_DATA(2),

  PAY_SEND_REQ_FAILED(3),

  PAY_TIMEOUT(4),

  PAY_EXCEPTION(5);

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
      case 0: return PAY_SUCCESS;
      case 1: return PAY_WRONG_FORMAT;
      case 2: return PAY_MISS_DATA;
      case 3: return PAY_SEND_REQ_FAILED;
      case 4: return PAY_TIMEOUT;
      case 5: return PAY_EXCEPTION;
      default: return null;
    }
  }

  @Override
  public int getValue() {
    return value;
  }
}