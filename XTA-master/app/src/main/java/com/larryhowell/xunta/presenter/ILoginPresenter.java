package com.larryhowell.xunta.presenter;

public interface ILoginPresenter {
    void login(String telephone);

    interface ILoginView {
        void onLoginResult(Boolean result, String info);
    }
}
