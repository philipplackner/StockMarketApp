package com.plcoding.stockmarketapp.data.local

class StockDaoFake: StockDao {

    private var companyListings = emptyList<CompanyListingEntity>()

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