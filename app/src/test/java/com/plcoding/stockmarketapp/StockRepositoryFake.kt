package com.plcoding.stockmarketapp

import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class StockRepositoryFake: StockRepository {

    var companyListingsToReturn = emptyList<CompanyListing>()
    var companyInfoToReturn = CompanyInfo(
        symbol = "test",
        description = "test description",
        name = "test name",
        industry = "test industry",
        country = "test country"
    )
    var intradayInfosToReturn = listOf(
        IntradayInfo(
            date = LocalDateTime.now(),
            close = 10.0
        )
    )

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Success(companyListingsToReturn))
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return Resource.Success(intradayInfosToReturn)
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return Resource.Success(companyInfoToReturn)
    }
}