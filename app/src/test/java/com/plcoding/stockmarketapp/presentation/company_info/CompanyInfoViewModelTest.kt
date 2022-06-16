package com.plcoding.stockmarketapp.presentation.company_info

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.plcoding.stockmarketapp.MainCoroutineRule
import com.plcoding.stockmarketapp.data.repository.StockRepositoryFake
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class CompanyInfoViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CompanyInfoViewModel
    private lateinit var repositoryFake: StockRepositoryFake

    @Before
    fun setUp() {
        repositoryFake = StockRepositoryFake()
        viewModel = CompanyInfoViewModel(
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    "symbol" to "GOOGL"
                )
            ),
            repository = repositoryFake
        )
    }

    @Test
    fun `Company and intraday info are properly mapped to state`() {
        val companyInfo = CompanyInfo(
            symbol = "GOOGL",
            description = "Google desc",
            name = "Google",
            country = "USA",
            industry = "Tech"
        )
        repositoryFake.companyInfoToReturn = companyInfo

        val intradayInfos = listOf(
            IntradayInfo(
                date = LocalDateTime.now(),
                close = 10.0
            )
        )
        repositoryFake.intradayInfosToReturn = intradayInfos

        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.company).isEqualTo(companyInfo)
        assertThat(viewModel.state.stockInfos).isEqualTo(intradayInfos)
    }
}