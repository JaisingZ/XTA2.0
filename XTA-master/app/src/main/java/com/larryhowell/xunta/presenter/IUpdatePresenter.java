package com.larryhowell.xunta.presenter;

public interface IUpdatePresenter {
    void getVersion();

    interface IUpdateView {
        void onGetVersionResult(Boolean result, String info);
    }
}
