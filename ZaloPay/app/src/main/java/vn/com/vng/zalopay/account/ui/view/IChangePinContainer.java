package vn.com.vng.zalopay.account.ui.view;

/**
 * Created by AnhHieu on 8/25/16.
 * *
 */
public interface IChangePinContainer {

    void nextPage();

    void onVerifySuccess();

    void onChangePinOverLimit();

    void initPagerContent(int index);
}
