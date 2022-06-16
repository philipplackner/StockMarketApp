package com.plcoding.stockmarketapp.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.local.CompanyListingEntity
import com.plcoding.stockmarketapp.data.local.StockDao
import com.plcoding.stockmarketapp.data.local.StockDaoFake
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.util.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class StockRepositoryImplTest {

    private val companyListings = (1..100).map {
        CompanyListing(
            name = "name$it",
            symbol = "symbol$it",
            exchange = "exchange$it"
        )
    }
    private val intradayInfos = (1..100).map {
        IntradayInfo(
            date = LocalDateTime.now(),
            close = it.toDouble()
        )
    }

    private lateinit var repository: StockRepositoryImpl
    private lateinit var api: StockApi
    private lateinit var db: StockDatabase
    private lateinit var stockDao: StockDao
    private lateinit var companyListingsParser: CSVParser<CompanyListing>
    private lateinit var intradayInfoParser: CSVParser<IntradayInfo>

    @Before
    fun setUp() {
        api = mockk(relaxed = true) {
            coEvery { getListings(any()) } returns mockk(relaxed = true)
        }
        stockDao = StockDaoFake()
        db = mockk(relaxed = true) {
            every { dao } returns stockDao
        }
        companyListingsParser = mockk(relaxed = true) {
            coEvery { parse(any()) } returns companyListings
        }
        intradayInfoParser = mockk(relaxed = true) {
            coEvery { parse(any()) } returns intradayInfos
        }
        repository = StockRepositoryImpl(
            api = api,
            db = db,
            companyListingsParser = companyListingsParser,
            intradayInfoParser = intradayInfoParser
        )
    }

    @Test
    fun `Test local database cache with fetch from remote set to true`() = runTest {
        val localListings = listOf(
            CompanyListingEntity(
                name = "test-name",
                symbol = "test-symbol",
                exchange = "test-exchange",
                id = 0
            )
        )
        stockDao.insertCompanyListings(localListings)

        repository.getCompanyListings(
            fetchFromRemote = true,
            query = ""
        ).test {
            val startLoading = awaitItem()
            assertThat((startLoading as Resource.Loading).isLoading).isTrue()

            val listingsFromDb = awaitItem()
            assertThat(listingsFromDb is Resource.Success).isTrue()
            assertThat(listingsFromDb.data).isEqualTo(localListings.map { it.toCompanyListing() })

            val remoteListingsFromDb = awaitItem()
            assertThat(remoteListingsFromDb is Resource.Success).isTrue()
            assertThat(remoteListingsFromDb.data).isEqualTo(
                stockDao.searchCompanyListing("").map { it.toCompanyListing() }
            )

            val stopLoading = awaitItem()
            assertThat((stopLoading as Resource.Loading).isLoading).isFalse()

            awaitComplete()
        }
    }
}