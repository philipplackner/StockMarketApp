package com.plcoding.stockmarketapp

import com.plcoding.stockmarketapp.data.local.CompanyListingEntity
import com.plcoding.stockmarketapp.data.local.StockDao

class StockDaoFake: StockDao {

    private var companyListings = listOf<CompanyListingEntity>()

    override suspend fun insertCompanyListings(companyListingEntities: List<CompanyListingEntity>) {
        companyListings = companyListings + companyListingEntities
    }

    override suspend fun clearCompanyListings() {
        companyListings = emptyList()
    }

    override suspend fun searchCompanyListing(query: String): List<CompanyListingEntity> {
        return companyListings.filter {
            it.name.lowercase().contains(query.lowercase())
        }
    }
}