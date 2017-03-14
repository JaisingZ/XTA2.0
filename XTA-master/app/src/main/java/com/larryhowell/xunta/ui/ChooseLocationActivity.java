package com.larryhowell.xunta.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.transition.Slide;
import android.view.Window;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.LocationSuggestion;
import com.larryhowell.xunta.common.Config;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseLocationActivity extends BaseActivity implements OnGetPoiSearchResultListener {
    @Bind(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide());

        setContentView(R.layout.activity_choose_location);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setupFloatingSearch();


    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            mSearchView.showProgress();

            PoiCitySearchOption option = new PoiCitySearchOption();
            option.city(Config.currentCity).pageCapacity(30).keyword(newQuery);

            PoiSearch search = PoiSearch.newInstance();
            search.setOnGetPoiSearchResultListener(ChooseLocationActivity.this);
            search.searchInCity(option);
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                LocationSuggestion suggestion = (LocationSuggestion) searchSuggestion;
                Intent intent = new Intent();
                intent.putExtra("location", suggestion);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onSearchAction(String query) {
            }
        });

        mSearchView.setOnBindSuggestionCallback(
                (suggestionView, leftIcon, textView, item, itemPosition) -> {
                    LocationSuggestion suggestion = (LocationSuggestion) item;

                    String textColor = "#ffffff";
                    String textLight = "#bfbfbf";

                    if (suggestion.getIsHistory()) {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_history_black_24dp, null));

                        Util.setIconColor(leftIcon, Color.parseColor(textColor));
                        leftIcon.setAlpha(.36f);
                    } else {
                        leftIcon.setAlpha(0.0f);
                        leftIcon.setImageDrawable(null);
                    }

                    textView.setTextColor(Color.parseColor(textColor));
                    String text = suggestion.getBody()
                            .replaceFirst(mSearchView.getQuery(),
                                    "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                    textView.setText(Html.fromHtml(text));
                });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                PoiCitySearchOption option = new PoiCitySearchOption();
                option.city(Config.currentCity).pageCapacity(30).keyword(Config.currentCityDetail);

                PoiSearch search = PoiSearch.newInstance();
                search.setOnGetPoiSearchResultListener(ChooseLocationActivity.this);
                search.searchInCity(option);
            }

            @Override
            public void onFocusCleared() {

            }
        });
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        List<PoiInfo> list = poiResult.getAllPoi();
        List<SearchSuggestion> newSuggestions = new ArrayList<>();

        if (list != null) {
            for (PoiInfo info : list) {
                newSuggestions.add(new LocationSuggestion(info));
            }
        }

        mSearchView.swapSuggestions(newSuggestions);

        mSearchView.hideProgress();
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.setSearchFocused(false)) {
            super.onBackPressed();
        }
    }
}
