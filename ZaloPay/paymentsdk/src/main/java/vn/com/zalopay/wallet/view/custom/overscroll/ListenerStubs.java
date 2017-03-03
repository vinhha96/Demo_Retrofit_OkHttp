package vn.com.zalopay.wallet.view.custom.overscroll;

public interface ListenerStubs {

    class OverScrollStateListenerStub implements IOverScrollStateListener {
        @Override
        public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
        }
    }

    class OverScrollUpdateListenerStub implements IOverScrollUpdateListener {
        @Override
        public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
        }
    }
}
