package com.appversal.appstorys

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _banners = MutableStateFlow<List<Campaign>>(emptyList())
    val banners: StateFlow<List<Campaign>> = _banners

    fun fetchData() {
        viewModelScope.launch {
            val accessToken = repository.getAccessToken()
            Log.d("accessToken", accessToken.toString())
            if (accessToken != null) {
                val campaignList = repository.getCampaigns(accessToken)
                Log.d("campaignList", campaignList.toString())
                val campaignsData = repository.getCampaignData(accessToken, campaignList)
                Log.d("campaignsData", campaignsData.toString())
                _banners.value = campaignsData.campaigns
            }
        }
    }
}