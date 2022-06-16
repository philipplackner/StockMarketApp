package com.plcoding.stockmarketapp.presentation.company_info

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.plcoding.stockmarketapp.MainCoroutineRule
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import kotlin.random.Random

@ExperimentalCoroutinesApi
class CompanyInfoViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CompanyInfoViewModel
    private lateinit var repository: StockRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = CompanyInfoViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf("symbol" to "GOOGL")
            ),
            repository = repository
        )
    }

    @Test
    fun `Company and intraday info properly mapped to state`() = runTest {
        val companyInfo = CompanyInfo(
            symbol = "GOOGL",
            description = "Google description",
            name = "Google",
            country = "USA",
            industry = "Tech"
        )
        coEvery { repository.getCompanyInfo("GOOGL") } returns Resource.Success(companyInfo)

        val intradayInfos = listOf(
            IntradayInfo(
                date = LocalDateTime.now(),
                close = Random.nextDouble()
            )
        )
        coEvery { repository.getIntradayInfo("GOOGL") } returns Resource.Success(intradayInfos)

        testScheduler.advanceUntilIdle()

        assertThat(viewModel.state.company).isEqualTo(companyInfo)
        assertThat(viewModel.state.stockInfos).isEqualTo(intradayInfos)
    }
}