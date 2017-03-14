package com.larryhowell.xunta.presenter;

public interface IUserInfoPresenter {
    void getUserInfo(String telephone);
    void updateNickname(String name);

    interface IUserInfoView {
        void onGetUserInfoResult(Boolean result, String info);
        void onUpdateNickname(Boolean result, String info);
    }
}
