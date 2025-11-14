package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Comprehensive test suite for Data Page rules
 * Tests all aspects of Data Page DSL functionality
 */
class DataPageTest extends Specification {

    def "should create basic data page with activity source"() {
        when:
        def dataPage = dataPage('D_CustomerList') {
            description 'Customer master data'
            sourceActivity 'LoadCustomers'
            scope 'Node'
            maxAge 3600
        }
        
        then:
        dataPage.name == 'D_CustomerList'
        dataPage.type == 'DataPage'
        dataPage.description == 'Customer master data'
        dataPage.sourceType == 'Activity'
        dataPage.dataSource == 'LoadCustomers'
        dataPage.scope == 'Node'
        dataPage.maxAge == 3600
        dataPage.refreshStrategy == 'Reload once per interaction'
    }

    def "should create data page with activity and parameters"() {
        when:
        def dataPage = dataPage('D_CustomersByRegion') {
            description 'Customers filtered by region and status'
            sourceActivity 'LoadCustomersByRegion', [
                Region: 'Northeast',
                Status: 'Active',
                MaxResults: '100'
            ]
            scope 'Requestor'
            maxAge 1800
            refresh 'Reload if older than specified age'
        }
        
        then:
        dataPage.name == 'D_CustomersByRegion'
        dataPage.sourceType == 'Activity'
        dataPage.dataSource == 'LoadCustomersByRegion'
        dataPage.sourceParameters.size() == 3
        dataPage.sourceParameters['Region'] == 'Northeast'
        dataPage.sourceParameters['Status'] == 'Active'
        dataPage.sourceParameters['MaxResults'] == '100'
        dataPage.scope == 'Requestor'
        dataPage.maxAge == 1800
        dataPage.refreshStrategy == 'Reload if older than specified age'
    }

    def "should create data page with connector source"() {
        when:
        def dataPage = dataPage('D_ExternalProducts') {
            description 'Product data from external API'
            connector 'ProductAPI', [
                category: 'electronics',
                limit: '50',
                includeImages: 'true'
            ]
            scope 'Application'
            maxAge 7200
            refresh 'Reload once per interaction'
        }
        
        then:
        dataPage.sourceType == 'Connector'
        dataPage.dataSource == 'ProductAPI'
        dataPage.sourceParameters['category'] == 'electronics'
        dataPage.sourceParameters['limit'] == '50'
        dataPage.sourceParameters['includeImages'] == 'true'
        dataPage.scope == 'Application'
        dataPage.maxAge == 7200
    }

    def "should create data page with report definition source"() {
        when:
        def dataPage = dataPage('D_SalesReport') {
            description 'Sales performance data'
            reportDefinition 'SalesPerformanceReport', [
                StartDate: '@today() - 30',
                EndDate: '@today()',
                Region: 'All'
            ]
            scope 'Thread'
            maxAge 3600
        }
        
        then:
        dataPage.sourceType == 'Report Definition'
        dataPage.dataSource == 'SalesPerformanceReport'
        dataPage.sourceParameters['StartDate'] == '@today() - 30'
        dataPage.sourceParameters['EndDate'] == '@today()'
        dataPage.sourceParameters['Region'] == 'All'
        dataPage.scope == 'Thread'
    }

    def "should create comprehensive customer data page"() {
        when:
        def dataPage = dataPage('D_CustomerMaster') {
            description 'Comprehensive customer master data with multiple refresh strategies'
            sourceActivity 'LoadCustomerMasterData', [
                IncludeInactive: 'false',
                IncludeAddresses: 'true',
                IncludeOrders: 'true',
                MaxOrderHistory: '12',
                SortBy: 'LastName'
            ]
            scope 'Node'
            maxAge 14400  // 4 hours
            refresh 'Reload if older than specified age'
            
            setDataProperty 'CacheStrategy', 'LRU'
            setDataProperty 'MaxCacheSize', '1000'
            setDataProperty 'PreloadOnStartup', 'true'
        }
        
        then:
        dataPage.name == 'D_CustomerMaster'
        dataPage.description == 'Comprehensive customer master data with multiple refresh strategies'
        dataPage.sourceType == 'Activity'
        dataPage.dataSource == 'LoadCustomerMasterData'
        dataPage.sourceParameters.size() == 5
        dataPage.sourceParameters['IncludeInactive'] == 'false'
        dataPage.sourceParameters['IncludeAddresses'] == 'true'
        dataPage.sourceParameters['IncludeOrders'] == 'true'
        dataPage.sourceParameters['MaxOrderHistory'] == '12'
        dataPage.sourceParameters['SortBy'] == 'LastName'
        dataPage.scope == 'Node'
        dataPage.maxAge == 14400
        dataPage.properties['CacheStrategy'] == 'LRU'
        dataPage.properties['MaxCacheSize'] == '1000'
        dataPage.properties['PreloadOnStartup'] == 'true'
    }

    def "should create real-time data page with frequent refresh"() {
        when:
        def dataPage = dataPage('D_StockPrices') {
            description 'Real-time stock price data'
            connector 'StockPriceAPI', [
                symbols: 'AAPL,GOOGL,MSFT,TSLA',
                fields: 'price,volume,change',
                realtime: 'true'
            ]
            scope 'Application'
            maxAge 60  // 1 minute
            refresh 'Reload if older than specified age'
            
            setDataProperty 'RefreshMode', 'Automatic'
            setDataProperty 'RefreshInterval', '30'
            setDataProperty 'FailoverStrategy', 'UseCache'
        }
        
        then:
        dataPage.name == 'D_StockPrices'
        dataPage.description == 'Real-time stock price data'
        dataPage.sourceType == 'Connector'
        dataPage.dataSource == 'StockPriceAPI'
        dataPage.sourceParameters['symbols'] == 'AAPL,GOOGL,MSFT,TSLA'
        dataPage.sourceParameters['realtime'] == 'true'
        dataPage.maxAge == 60
        dataPage.properties['RefreshMode'] == 'Automatic'
        dataPage.properties['RefreshInterval'] == '30'
        dataPage.properties['FailoverStrategy'] == 'UseCache'
    }

    def "should create data page with different scope options"() {
        when:
        def requestorScopedPage = dataPage('D_UserPreferences') {
            description 'User-specific preferences'
            sourceActivity 'LoadUserPreferences', [UserID: '@operator.pyUserName']
            scope 'Requestor'
            maxAge 86400  // 24 hours
        }
        
        def threadScopedPage = dataPage('D_SessionData') {
            description 'Session-specific data'
            sourceActivity 'LoadSessionData'
            scope 'Thread'
            maxAge 1800  // 30 minutes
        }
        
        def nodeScopedPage = dataPage('D_SystemConfig') {
            description 'System configuration data'
            sourceActivity 'LoadSystemConfig'
            scope 'Node'
            maxAge 43200  // 12 hours
        }
        
        def applicationScopedPage = dataPage('D_GlobalSettings') {
            description 'Global application settings'
            sourceActivity 'LoadGlobalSettings'
            scope 'Application'
            maxAge 86400  // 24 hours
        }
        
        then:
        requestorScopedPage.scope == 'Requestor'
        threadScopedPage.scope == 'Thread'
        nodeScopedPage.scope == 'Node'
        applicationScopedPage.scope == 'Application'
        
        requestorScopedPage.maxAge == 86400
        threadScopedPage.maxAge == 1800
        nodeScopedPage.maxAge == 43200
        applicationScopedPage.maxAge == 86400
    }

    def "should create data page with various refresh strategies"() {
        when:
        def autoRefreshPage = dataPage('D_AutoRefresh') {
            description 'Automatically refreshed data'
            sourceActivity 'LoadData'
            refresh 'Reload if older than specified age'
            maxAge 3600
        }
        
        def oncePerInteractionPage = dataPage('D_OncePerInteraction') {
            description 'Refreshed once per interaction'
            sourceActivity 'LoadData'
            refresh 'Reload once per interaction'
        }
        
        def manualRefreshPage = dataPage('D_ManualRefresh') {
            description 'Manually refreshed data'
            sourceActivity 'LoadData'
            refresh 'Do not reload'
        }
        
        then:
        autoRefreshPage.refreshStrategy == 'Reload if older than specified age'
        autoRefreshPage.maxAge == 3600
        
        oncePerInteractionPage.refreshStrategy == 'Reload once per interaction'
        
        manualRefreshPage.refreshStrategy == 'Do not reload'
    }

    def "should create lookup data page for reference data"() {
        when:
        def dataPage = dataPage('D_CountryList') {
            description 'List of countries for dropdown selections'
            sourceActivity 'LoadCountries', [
                IncludeRegions: 'true',
                ActiveOnly: 'true',
                SortBy: 'Name'
            ]
            scope 'Application'
            maxAge 604800  // 1 week (reference data rarely changes)
            refresh 'Reload if older than specified age'
            
            setDataProperty 'DataType', 'ReferenceData'
            setDataProperty 'CacheLevel', 'High'
            setDataProperty 'PreloadOnStartup', 'true'
            setDataProperty 'CompressionEnabled', 'true'
        }
        
        then:
        dataPage.name == 'D_CountryList'
        dataPage.description == 'List of countries for dropdown selections'
        dataPage.sourceParameters['IncludeRegions'] == 'true'
        dataPage.sourceParameters['ActiveOnly'] == 'true'
        dataPage.scope == 'Application'
        dataPage.maxAge == 604800
        dataPage.properties['DataType'] == 'ReferenceData'
        dataPage.properties['CacheLevel'] == 'High'
        dataPage.properties['CompressionEnabled'] == 'true'
    }

    def "should create data page with complex API integration"() {
        when:
        def dataPage = dataPage('D_WeatherForecast') {
            description 'Weather forecast data from external service'
            connector 'WeatherAPI', [
                location: '.Customer.Address.ZipCode',
                days: '7',
                units: 'imperial',
                includehourly: 'true',
                lang: 'en'
            ]
            scope 'Requestor'
            maxAge 10800  // 3 hours
            refresh 'Reload if older than specified age'
            
            setDataProperty 'APIVersion', '3.0'
            setDataProperty 'TimeoutSeconds', '30'
            setDataProperty 'RetryAttempts', '3'
            setDataProperty 'FallbackData', 'D_DefaultWeather'
            setDataProperty 'ErrorHandling', 'UseCache'
        }
        
        then:
        dataPage.sourceType == 'Connector'
        dataPage.dataSource == 'WeatherAPI'
        dataPage.sourceParameters['location'] == '.Customer.Address.ZipCode'
        dataPage.sourceParameters['days'] == '7'
        dataPage.sourceParameters['includehourly'] == 'true'
        dataPage.properties['APIVersion'] == '3.0'
        dataPage.properties['TimeoutSeconds'] == '30'
        dataPage.properties['FallbackData'] == 'D_DefaultWeather'
    }

    def "should create data page with rule inheritance properties"() {
        when:
        def dataPage = dataPage('D_InheritedData') {
            description 'Data page with rule inheritance properties'
            className 'MyApp-Customer-Data'
            setStatus 'Final'
            setAvailable true
            
            sourceActivity 'LoadInheritedData', [Type: 'Standard']
            scope 'Node'
            maxAge 7200
            
            setDataProperty 'RulesetName', 'DataManagement'
            setDataProperty 'RulesetVersion', '01.01.01'
            setDataProperty 'BusinessPurpose', 'Customer data caching'
            setDataProperty 'PerformanceLevel', 'High'
        }
        
        then:
        dataPage.name == 'D_InheritedData'
        dataPage.className == 'MyApp-Customer-Data'
        dataPage.status == 'Final'
        dataPage.isAvailable == true
        dataPage.properties['RulesetName'] == 'DataManagement'
        dataPage.properties['BusinessPurpose'] == 'Customer data caching'
        dataPage.properties['PerformanceLevel'] == 'High'
        dataPage.sourceParameters['Type'] == 'Standard'
    }

    def "should create parameterized data page for dynamic queries"() {
        when:
        def dataPage = dataPage('D_DynamicProductSearch') {
            description 'Dynamic product search based on multiple criteria'
            sourceActivity 'SearchProducts', [
                Category: '.SearchCriteria.Category',
                PriceMin: '.SearchCriteria.PriceMin',
                PriceMax: '.SearchCriteria.PriceMax',
                Brand: '.SearchCriteria.Brand',
                Rating: '.SearchCriteria.MinRating',
                InStock: '.SearchCriteria.InStockOnly',
                SortBy: '.SearchCriteria.SortField',
                SortOrder: '.SearchCriteria.SortDirection',
                PageSize: '.SearchCriteria.PageSize',
                PageNumber: '.SearchCriteria.PageNumber'
            ]
            scope 'Thread'
            maxAge 900  // 15 minutes
            refresh 'Reload if older than specified age'
            
            setDataProperty 'CacheStrategy', 'ParameterBased'
            setDataProperty 'MaxParameterCombinations', '100'
            setDataProperty 'SearchOptimization', 'true'
        }
        
        then:
        dataPage.name == 'D_DynamicProductSearch'
        dataPage.sourceParameters.size() == 10
        dataPage.sourceParameters['Category'] == '.SearchCriteria.Category'
        dataPage.sourceParameters['PriceMin'] == '.SearchCriteria.PriceMin'
        dataPage.sourceParameters['SortBy'] == '.SearchCriteria.SortField'
        dataPage.scope == 'Thread'
        dataPage.properties['CacheStrategy'] == 'ParameterBased'
        dataPage.properties['MaxParameterCombinations'] == '100'
    }

    def "should create data page for aggregated reporting data"() {
        when:
        def dataPage = dataPage('D_SalesAnalytics') {
            description 'Sales analytics and KPI data'
            reportDefinition 'SalesAnalyticsReport', [
                DateRange: '.ReportCriteria.DateRange',
                Region: '.ReportCriteria.Region',
                ProductLine: '.ReportCriteria.ProductLine',
                SalesRep: '.ReportCriteria.SalesRep',
                GroupBy: '.ReportCriteria.GroupBy',
                Metrics: 'Revenue,Units,Margin,Growth',
                Format: 'Summary'
            ]
            scope 'Application'
            maxAge 21600  // 6 hours
            refresh 'Reload if older than specified age'
            
            setDataProperty 'ReportType', 'Analytics'
            setDataProperty 'AggregationLevel', 'Daily'
            setDataProperty 'DataRetention', '90'
            setDataProperty 'ExportFormats', 'PDF,Excel,CSV'
            setDataProperty 'ScheduledRefresh', 'true'
        }
        
        then:
        dataPage.sourceType == 'Report Definition'
        dataPage.dataSource == 'SalesAnalyticsReport'
        dataPage.sourceParameters['DateRange'] == '.ReportCriteria.DateRange'
        dataPage.sourceParameters['Metrics'] == 'Revenue,Units,Margin,Growth'
        dataPage.properties['ReportType'] == 'Analytics'
        dataPage.properties['AggregationLevel'] == 'Daily'
        dataPage.properties['ScheduledRefresh'] == 'true'
    }

    def "should create data page with error handling and fallback"() {
        when:
        def dataPage = dataPage('D_ResilientData') {
            description 'Data page with comprehensive error handling'
            connector 'ExternalDataService', [
                endpoint: 'customers',
                timeout: '30',
                retries: '3'
            ]
            scope 'Node'
            maxAge 3600
            refresh 'Reload if older than specified age'
            
            setDataProperty 'ErrorHandling', 'Graceful'
            setDataProperty 'FallbackDataPage', 'D_CachedCustomers'
            setDataProperty 'OfflineMode', 'true'
            setDataProperty 'CircuitBreakerEnabled', 'true'
            setDataProperty 'HealthCheckEndpoint', '/health'
            setDataProperty 'AlertOnFailure', 'true'
            setDataProperty 'MaxConsecutiveFailures', '5'
        }
        
        then:
        dataPage.properties['ErrorHandling'] == 'Graceful'
        dataPage.properties['FallbackDataPage'] == 'D_CachedCustomers'
        dataPage.properties['OfflineMode'] == 'true'
        dataPage.properties['CircuitBreakerEnabled'] == 'true'
        dataPage.properties['MaxConsecutiveFailures'] == '5'
    }

    def "should create data page for microservice integration"() {
        when:
        def dataPage = dataPage('D_MicroserviceData') {
            description 'Data from microservice with advanced configuration'
            connector 'CustomerMicroservice', [
                version: 'v2',
                fields: 'id,name,email,status,preferences',
                filter: '.RequestFilter',
                pagination: 'true',
                pageSize: '50'
            ]
            scope 'Thread'
            maxAge 600  // 10 minutes
            refresh 'Reload if older than specified age'
            
            setDataProperty 'ServiceType', 'Microservice'
            setDataProperty 'LoadBalancing', 'RoundRobin'
            setDataProperty 'HealthChecks', 'true'
            setDataProperty 'Authentication', 'OAuth2'
            setDataProperty 'RateLimiting', 'true'
            setDataProperty 'Monitoring', 'true'
            setDataProperty 'TracingEnabled', 'true'
        }
        
        then:
        dataPage.sourceType == 'Connector'
        dataPage.sourceParameters['version'] == 'v2'
        dataPage.sourceParameters['fields'] == 'id,name,email,status,preferences'
        dataPage.sourceParameters['pagination'] == 'true'
        dataPage.properties['ServiceType'] == 'Microservice'
        dataPage.properties['LoadBalancing'] == 'RoundRobin'
        dataPage.properties['Authentication'] == 'OAuth2'
        dataPage.properties['TracingEnabled'] == 'true'
    }
}
