package com.larryhowell.xunta.presenter;

import com.larryhowell.xunta.bean.Plan;

public interface IPlanPresenter {
    void makePlan(Plan plan);
    void cancelPlan(String telephone);
    void getPlan(String telephone);

    interface IPlanView {
        void onMakePlanResult(Boolean result, String info);
        void onCancelPlanResult(Boolean result, String info);
        void onGetPlanResult(Boolean result, String info);
    }
}
