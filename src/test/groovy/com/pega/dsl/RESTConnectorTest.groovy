package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class RESTConnectorTest extends Specification {

    def "should create REST connector with basic properties"() {
        when:
        def connector = restConnector('GetCustomer') {
            description 'Get customer details from external service'
            url 'https://api.example.com/customers/{id}'
            get()
            authentication 'OAuth2Profile'
        }

        then:
        connector.name == 'GetCustomer'
        connector.description == 'Get customer details from external service'
        connector.url == 'https://api.example.com/customers/{id}'
        connector.method == 'GET'
        connector.authProfile == 'OAuth2Profile'
    }

    def "should support different HTTP methods"() {
        when:
        def postConn = restConnector('CreateCustomer') { post() }
        def putConn = restConnector('UpdateCustomer') { put() }
        def deleteConn = restConnector('DeleteCustomer') { delete() }
        def patchConn = restConnector('PatchCustomer') { patch() }

        then:
        postConn.method == 'POST'
        putConn.method == 'PUT'
        deleteConn.method == 'DELETE'
        patchConn.method == 'PATCH'
    }

    def "should configure headers"() {
        when:
        def connector = restConnector('ApiWithHeaders') {
            header 'Accept', 'application/json'
            header 'Content-Type', 'application/json'
            header 'X-Custom-Header', 'MyValue'
        }

        then:
        connector.headers.size() == 3
        connector.headers['Accept'] == 'application/json'
        connector.headers['X-Custom-Header'] == 'MyValue'
    }

    def "should configure request mapping"() {
        when:
        def connector = restConnector('CreateOrder') {
            post()
            url 'https://api.example.com/orders'
            requestMapping {
                map '.Order.ID', 'orderId'
                map '.Order.Amount', 'orderAmount'
                set 'sourceSystem', '"Pega"'
            }
        }

        then:
        connector.requestMapping.size() == 3
        connector.requestMapping['orderId'] == '.Order.ID'
        connector.requestMapping['orderAmount'] == '.Order.Amount'
        connector.requestMapping['sourceSystem'] == '"Pega"'
    }

    def "should configure response mapping"() {
        when:
        def connector = restConnector('GetProduct') {
            get()
            url 'https://api.example.com/products/{id}'
            responseMapping {
                map 'product.name', '.Product.Name'
                map 'product.price.amount', '.Product.Price'
                map 'product.price.currency', '.Product.Currency'
                set '.LastUpdated', '@now()'
            }
        }

        then:
        connector.responseMapping.size() == 4
        connector.responseMapping['.Product.Name'] == 'product.name'
        connector.responseMapping['.Product.Price'] == 'product.price.amount'
        connector.responseMapping['.LastUpdated'] == '@now()'
    }
    
    def "should create a comprehensive REST connector"() {
        when:
        def connector = restConnector('SyncInventory') {
            description 'Sync inventory levels with external warehouse system'
            put()
            url 'https://warehouse.api/inventory/{sku}'
            authentication 'WarehouseAuth'
            
            header 'X-Transaction-ID', 'UUID()'
            
            requestMapping {
                map '.Product.SKU', 'sku'
                map '.Product.StockLevel', 'quantity'
                set 'updateSource', '"PegaSystem"'
            }
            
            responseMapping {
                map 'status', '.SyncStatus'
                map 'lastUpdated', '.LastSyncTime'
            }
        }

        then:
        connector.method == 'PUT'
        connector.url.contains('{sku}')
        connector.authProfile == 'WarehouseAuth'
        connector.headers.containsKey('X-Transaction-ID')
        connector.requestMapping.size() == 3
        connector.responseMapping.size() == 2
    }
}
