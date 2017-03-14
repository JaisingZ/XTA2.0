package com.larryhowell.xunta.presenter;

public interface IBindPresenter {
    void bind(String telephone);
    void bindConfirm(boolean accept, String telephone);
    void getBindList();

    interface IBindView {
        void onBindResult(Boolean result, String info);
        void onBindConfirmResult(Boolean result, String info);
        void OnGetBindListResult(Boolean result, String info);
    }
}