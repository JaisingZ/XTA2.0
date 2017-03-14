package com.larryhowell.xunta.presenter;

public interface IShareLocationPresenter {
    void requestLocation(String target);
    void confirmShare(boolean accept, String nickname, String sender);

    interface IShareLocationView {
        void requestLocationResult(Boolean result, String info);
        void confirmShareResult(Boolean result, String info);
    }
}
